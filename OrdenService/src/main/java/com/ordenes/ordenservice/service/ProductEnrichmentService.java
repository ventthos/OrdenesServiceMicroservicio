package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.models.ProductItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEnrichmentService {

    private final RestTemplate restTemplate;
    private static final String PRODUCT_SERVICE_URL = "http://product-service/productos";

    public void enrichOrder(Order order) {
        if (order == null || order.getProducts() == null || order.getProducts().isEmpty()) {
            return;
        }
        enrichOrders(List.of(order));
    }

    public void enrichOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        try {
            log.debug("Obteniendo productos para mostrar nombres");
            ResponseEntity<Map> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> productsData = (List<Map<String, Object>>) response.getBody().get("data");
                if (productsData != null) {
                    Map<String, String> productNamesMap = new HashMap<>();
                    for (Map<String, Object> product : productsData) {
                        Object id = product.get("id");
                        Object name = product.get("name");
                        if (id != null && name != null) {
                            productNamesMap.put(id.toString(), name.toString());
                        }
                    }

                    for (Order order : orders) {
                        if (order.getProducts() != null) {
                            for (ProductItem item : order.getProducts()) {
                                String name = productNamesMap.get(item.getProductId());
                                if (name != null) {
                                    item.setName(name);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al enriquecer las órdenes con nombres de productos: {}", e.getMessage());
        }
    }
}
