package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.UpdateOrderDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.models.ProductItem;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateOrderService {

    private final OrdenRepository orderRepository;
    private final OrdenProducer ordenProducer;

    public Order execute(String id, UpdateOrderDto data) {
        log.info("Recibida solicitud para actualizar orden ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fallo al actualizar orden: La orden con ID {} no existe.", id);
                    return new NoSuchElementException("No se encontró la orden con ID: " + id);
                });

        // Guardamos los productos viejos antes de actualizar
        List<ProductItem> oldProducts = order.getProducts() != null ? new ArrayList<>(order.getProducts()) : new ArrayList<>();

        // Actualizamos los campos
        String oldStatus = order.getStatus();

        if (data.getOrderCode() != null) order.setOrderCode(data.getOrderCode());
        if (data.getOrderDate() != null) order.setOrderDate(data.getOrderDate());
        if (data.getTotalAmount() != null) order.setTotalAmount(data.getTotalAmount());
        if (data.getStatus() != null) order.setStatus(data.getStatus());
        if (data.getUserId() != null) order.setUser(data.getUserId());
        if (data.getProducts() != null) order.setProducts(data.getProducts());

        try {
            Order savedOrder = orderRepository.save(order);

            if (data.getStatus() != null && !data.getStatus().equals(oldStatus)) {
                ordenProducer.sendToInformChangeInStatus(order.getId(), oldStatus, data.getStatus());
            }

            log.debug("Notificando cambios en el inventario para la orden {}. Productos viejos: {}, Productos nuevos: {}", 
                    id, oldProducts.size(), savedOrder.getProducts() != null ? savedOrder.getProducts().size() : 0);

            if (!areProductsEqual(oldProducts, savedOrder.getProducts())) {

                log.debug("Se detectaron cambios reales en productos, enviando evento a Kafka...");

                ordenProducer.modifyProductExistence(
                        savedOrder.getId(),
                        savedOrder.getProducts(),
                        oldProducts
                );

            } else {
                log.debug("Los productos no cambiaron, no se envía evento a Kafka.");
            }
            
            log.info("Orden {} actualizada exitosamente en MongoDB.", savedOrder.getId());
            return savedOrder;

        } catch (Exception e) {
            log.error("Fallo al actualizar la orden {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error al actualizar la orden en el sistema", e);
        }
    }

    private boolean areProductsEqual(List<ProductItem> list1, List<ProductItem> list2) {

        if (list1 == null) list1 = Collections.emptyList();
        if (list2 == null) list2 = Collections.emptyList();

        Map<String, Integer> map1 = list1.stream()
                .collect(Collectors.toMap(
                        ProductItem::getProductId,
                        ProductItem::getQuantity,
                        Integer::sum
                ));

        Map<String, Integer> map2 = list2.stream()
                .collect(Collectors.toMap(
                        ProductItem::getProductId,
                        ProductItem::getQuantity,
                        Integer::sum
                ));

        return map1.equals(map2);
    }
}
