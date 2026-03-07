package com.products.ordenservice.service;

import com.products.ordenservice.models.Order;
import com.products.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetOrderByIdService {

    private final OrdenRepository orderRepository;

    public Order execute(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con el ID: " + id));
    }
}