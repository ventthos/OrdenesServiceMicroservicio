package com.ordenes.ordenservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ordenes.ordenservice.dto.CreateOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdenProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "order_retry_jobs";

    public void sendToRetry(CreateOrderDto dto) {
        dto.setFromRetry(true);
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC, message);
        } catch (Exception e) {
            throw new RuntimeException("Error enviando a Kafka", e);
        }
    }
}