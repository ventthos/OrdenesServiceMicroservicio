package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckProductInOrdersService {
    private final OrdenRepository ordenRepository;

    public boolean execute(String productId) {
        log.info("Checking if product {} exists in any order", productId);
        return ordenRepository.existsByProductsProductId(productId);
    }
}
