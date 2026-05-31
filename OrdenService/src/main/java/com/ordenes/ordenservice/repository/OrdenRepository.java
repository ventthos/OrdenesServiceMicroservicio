package com.ordenes.ordenservice.repository;

import com.ordenes.ordenservice.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrdenRepository extends MongoRepository<Order, String> {
    List<Order> findByUser(String userEmail);
    boolean existsByProductsProductId(String productId);
}