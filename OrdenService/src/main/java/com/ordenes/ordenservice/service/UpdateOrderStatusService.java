package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.UpdateOrderStatusDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateOrderStatusService {

    private final OrdenRepository orderRepository;

    public Order execute(String id, UpdateOrderStatusDto data) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la orden con ID: " + id));

        order.setStatus(data.getStatus());

        return orderRepository.save(order);
    }
}