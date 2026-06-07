package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.exceptionhandler.RetryScheduledException;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateOrderService {

    private final OrdenRepository orderRepository;
    private final OrdenProducer ordenProducer;
    private final org.springframework.web.client.RestTemplate restTemplate;

    public Order execute(CreateOrderDto data) {
        log.info("Recibida solicitud para crear orden. Código: {}, Usuario: {}",
                data.getOrderCode(), data.getUserId());

        // Envolvemos TODO el flujo de negocio en el try principal para asegurar el reintento ante cualquier fallo técnico
        try {

            // 1. Validar Stock
            List<String> outOfStockProducts = new java.util.ArrayList<>();
            if (data.getProducts() != null) {
                for (com.ordenes.ordenservice.models.ProductItem item : data.getProducts()) {
                    try {
                        String url = "http://product-service/productos/" + item.getProductId();
                        org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            java.util.Map responseBody = response.getBody();
                            java.util.Map productData = (java.util.Map) responseBody.get("data");
                            if (productData != null) {
                                int stock = (int) productData.get("quantity");
                                String productName = (String) productData.get("name");
                                if (stock < item.getQuantity()) {
                                    outOfStockProducts.add(productName);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error al verificar stock para el producto {}: {}", item.getProductId(), e.getMessage());
                        // Lanzamos una excepción que será atrapada por el catch principal externo
                        throw new RuntimeException("Error en la comunicación con el servicio de productos", e);
                    }
                }
            }

            // Si es un error de negocio (falta de stock real), lanzamos IllegalArgumentException
            if (!outOfStockProducts.isEmpty()) {
                String message = "hace falta stock de los productos: " + String.join(", ", outOfStockProducts);
                log.warn(message);
                throw new IllegalArgumentException(message);
            }

            // 2. Persistir Orden
            Order order = Order.builder()
                    .orderCode(data.getOrderCode())
                    .orderDate(data.getOrderDate())
                    .totalAmount(data.getTotalAmount())
                    .status("Pendiente")
                    .user(data.getUserId())
                    .products(data.getProducts())
                    .debt(data.getTotalAmount())
                    .build();

            log.debug("Procesando orden {} con un total de ${} y {} productos.",
                    data.getOrderCode(), data.getTotalAmount(),
                    (data.getProducts() != null ? data.getProducts().size() : 0));

            Order savedOrder = orderRepository.save(order);
            ordenProducer.modifyProductExistence(savedOrder.getId(), savedOrder.getProducts(), List.of());
            log.info("Orden guardada exitosamente en MongoDB. ID generado: {}, Status inicial: {}",
                    savedOrder.getId(), savedOrder.getStatus());

            return savedOrder;

        } catch (IllegalArgumentException e) {
            // Error de negocio (Falta de stock): No debe reintentarse en la cola, se propaga directamente
            throw e;
        } catch (Exception e) {
            // Fallos de infraestructura/comunicación (Base de datos caída, error de red de RestTemplate, etc.)
            log.error("Fallo durante el procesamiento de la orden {}. Error: {}", data.getOrderCode(), e.getMessage(), e);
            if (!data.isFromRetry()) {
                ordenProducer.sendToRetry(data);
                throw new RetryScheduledException(
                        "Hubo un error. Se reintentará crear la orden lo más pronto posible"
                );
            }
            throw new RuntimeException("Error al crear la orden en el sistema", e);
        }
    }
}