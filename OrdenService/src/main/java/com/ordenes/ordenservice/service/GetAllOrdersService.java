package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetAllOrdersService {
    private final OrdenRepository ordenRepository;
    private final ProductEnrichmentService productEnrichmentService;

    public List<Order> execute() {
        log.info("Obteniendo todas las órdenes");
        List<Order> orders = ordenRepository.findAll();
        productEnrichmentService.enrichOrders(orders);
        return orders;
    }
}
