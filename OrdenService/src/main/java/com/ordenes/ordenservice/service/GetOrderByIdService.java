package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class GetOrderByIdService {

    private final OrdenRepository orderRepository;
    private final ProductEnrichmentService productEnrichmentService;

    public Order execute(String id) {
        log.info("Consultando información de la orden con ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fallo de consulta: No se encontró ninguna orden con el ID: {}", id);
                    return new NoSuchElementException("Orden no encontrada con el ID: " + id);
                });

        productEnrichmentService.enrichOrder(order);

        log.info("Orden {} recuperada exitosamente. Total: ${}",
                order.getOrderCode(), order.getTotalAmount());
        return order;
    }
}