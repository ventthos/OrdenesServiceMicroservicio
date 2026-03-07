package com.products.ordenservice.service;

import com.products.ordenservice.dto.CreateOrderDto;
import com.products.ordenservice.models.Order;
import com.products.ordenservice.repository.OrdenRepository;
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