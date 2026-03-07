package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateOrderService {

    private final OrdenRepository orderRepository;

    public Order execute(CreateOrderDto data) {
        Order order = Order.builder()
                .orderCode(data.getOrderCode())
                .orderDate(data.getOrderDate())
                .totalAmount(data.getTotalAmount())
                .status(data.getStatus())
                .user(data.getUserId())
                .products(data.getProducts())
                .build();

        return orderRepository.save(order);
    }
}