package com.products.ordenservice.service;

import com.products.ordenservice.dto.UpdateOrderStatusDto;
import com.products.ordenservice.models.Order;
import com.products.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateOrderStatusService {

    private final OrdenRepository orderRepository;

    public Order execute(String id, UpdateOrderStatusDto data) {
        // 1. Buscar la orden existente
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la orden con ID: " + id));

        // 2. Actualizar solo el estatus
        order.setStatus(data.getStatus());

        // 3. Persistir el cambio
        return orderRepository.save(order);
    }
}