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
    public Order execute(CreateOrderDto data) {
        log.info("Recibida solicitud para crear orden. Código: {}, Usuario: {}",
                data.getOrderCode(), data.getUserId());

        try {
            Order order = Order.builder()
                    .orderCode(data.getOrderCode())
                    .orderDate(data.getOrderDate())
                    .totalAmount(data.getTotalAmount())
                    .status(data.getStatus())
                    .user(data.getUserId())
                    .products(data.getProducts())
                    .build();

            log.debug("Procesando orden {} con un total de ${} y {} productos.",
                    data.getOrderCode(), data.getTotalAmount(),
                    (data.getProducts() != null ? data.getProducts().size() : 0));

            Order savedOrder = orderRepository.save(order);
            ordenProducer.modifyProductExistence(savedOrder.getId(), savedOrder.getProducts(), List.of());
            log.info("Orden guardada exitosamente en MongoDB. ID generado: {}, Status inicial: {}",
                    savedOrder.getId(), savedOrder.getStatus());

            return savedOrder;

        } catch (Exception e) {
            log.error("Fallo al persistir la orden {}. Error: {}", data.getOrderCode(), e.getMessage(), e);
            if (!data.isFromRetry()) {
                ordenProducer.sendToRetry(data);
            }
            throw new RuntimeException("Error al crear la orden en el sistema", e);
        }
    }
}