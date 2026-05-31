package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateOrderService {

    private final OrdenRepository orderRepository;
    private final OrdenProducer ordenProducer;
    private final org.springframework.web.client.RestTemplate restTemplate;

    public Order execute(CreateOrderDto data) {
        log.info("Recibida solicitud para crear orden. Código: {}, Usuario: {}",
                data.getOrderCode(), data.getUserId());

        // Validar Stock
        List<String> outOfStockProducts = new java.util.ArrayList<>();
        if (data.getProducts() != null) {
            for (com.ordenes.ordenservice.models.ProductItem item : data.getProducts()) {
                try {
                    String url = "http://productservice/productos/" + item.getProductId();
                    // Usamos un Map para evitar crear un DTO extra si no es necesario,
                    // pero necesitamos acceder a data.quantity y data.name
                    org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        java.util.Map responseBody = response.getBody();
                        java.util.Map productData = (java.util.Map) responseBody.get("data");
                        if (productData != null) {
                            int stock = (int) productData.get("quantity");
                            String productName = (String) productData.get("name");
                            if (stock < item.getQuantity()) {
                                outOfStockProducts.add(productName);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error al verificar stock para el producto {}: {}", item.getProductId(), e.getMessage());
                    throw new RuntimeException("Error al verificar stock con el servicio de productos", e);
                }
            }
        }

        if (!outOfStockProducts.isEmpty()) {
            String message = "hace falta stock de los productos: " + String.join(", ", outOfStockProducts);
            log.warn(message);
            throw new IllegalArgumentException(message);
        }

        try {
            Order order = Order.builder()
                    .orderCode(data.getOrderCode())
                    .orderDate(data.getOrderDate())
                    .totalAmount(data.getTotalAmount())
                    .status("Pendiente")
                    .user(data.getUserId())
                    .products(data.getProducts())
                    .debt(data.getTotalAmount())
                    .build();

            log.debug("Procesando orden {} con un total de ${} y {} productos.",
                    data.getOrderCode(), data.getTotalAmount(),
                    (data.getProducts() != null ? data.getProducts().size() : 0));

            Order savedOrder = orderRepository.save(order);
            ordenProducer.modifyProductExistence(savedOrder.getId(), savedOrder.getProducts(), List.of());
            log.info("Orden guardada exitosamente en MongoDB. ID generado: {}, Status inicial: {}",
                    savedOrder.getId(), savedOrder.getStatus());

            return savedOrder;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Fallo al persistir la orden {}. Error: {}", data.getOrderCode(), e.getMessage(), e);
            if (!data.isFromRetry()) {
                ordenProducer.sendToRetry(data);
            }
            throw new RuntimeException("Error al crear la orden en el sistema", e);
        }
    }
}