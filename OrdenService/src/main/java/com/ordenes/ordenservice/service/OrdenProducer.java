package com.ordenes.ordenservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.dto.InventoryChangeDto;
import com.ordenes.ordenservice.dto.StatusChangeDto;
import com.ordenes.ordenservice.models.ProductItem;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrdenProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "order_retry_jobs";
    private static final String TOPIC_EMAIL_STATUS = "order_status_change";
    private static final String TOPIC_INVENTORY_CHANGE = "inventory_change";

    public void sendToRetry(CreateOrderDto dto) {
        dto.setFromRetry(true);
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC, message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando a Kafka", e);
        }
    }

    public void sendToInformChangeInStatus(String orderId, String oldStatus, String newStatus){
        StatusChangeDto dataToSend = StatusChangeDto.builder()
                .orderId(orderId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .build();
        try {
            String message = objectMapper.writeValueAsString(dataToSend);
            kafkaTemplate.send(TOPIC_EMAIL_STATUS, message);
        }
        catch (Exception e) {
            throw new RuntimeException("Error enviando a Kafka", e);
        }
    }

    public void modifyProductExistence(String orderId, List<ProductItem> newProducts, List<ProductItem> oldProducts){
        if(newProducts.isEmpty() && oldProducts.isEmpty()){
            return;
        }

        InventoryChangeDto dataToSend = InventoryChangeDto.builder()
                .orderId(orderId)
                .newProducts(newProducts)
                .oldProducts(oldProducts)
                .build();
        try {
            String message = objectMapper.writeValueAsString(dataToSend);
            kafkaTemplate.send(TOPIC_INVENTORY_CHANGE, message);
        }
        catch (Exception e){
            throw new RuntimeException("Error enviando a Kafka", e);
        }
    }
}