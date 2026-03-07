package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetOrdersByUserService {

    private final OrdenRepository orderRepository;

    public List<Order> execute(String userEmail) {
        List<Order> orders = orderRepository.findByUser(userEmail);

        if (orders.isEmpty()) {
            throw new RuntimeException("No se encontraron órdenes para el usuario: " + userEmail);
        }

        return orders;
    }
}