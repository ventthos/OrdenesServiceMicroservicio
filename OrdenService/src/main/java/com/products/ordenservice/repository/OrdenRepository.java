package com.products.ordenservice.repository;

import com.products.ordenservice.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrdenRepository extends MongoRepository<Order, String> {
}