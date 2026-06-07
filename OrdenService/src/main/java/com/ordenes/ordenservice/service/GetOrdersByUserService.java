package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class GetOrdersByUserService {

    private final OrdenRepository orderRepository;
    private final ProductEnrichmentService productEnrichmentService;

    public List<Order> execute(String userEmail) {
        log.info("Consultando historial de órdenes para el usuario: {}", userEmail);

        List<Order> orders = orderRepository.findByUser(userEmail);

        if (orders.isEmpty()) {
            log.warn("El usuario {} no tiene órdenes registradas en el sistema.", userEmail);
            throw new NoSuchElementException("No se encontraron órdenes para el usuario: " + userEmail);
        }

        productEnrichmentService.enrichOrders(orders);

        log.info("Consulta exitosa: Se encontraron {} órdenes para el usuario {}.", orders.size(), userEmail);
        return orders;
    }
}