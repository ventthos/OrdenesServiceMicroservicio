package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
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