package com.ordenes.ordenservice.service;

import com.ordenes.ordenservice.dto.UpdateOrderStatusDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.repository.OrdenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateOrderStatusService {
    private final OrdenProducer ordenProducer;
    private final OrdenRepository orderRepository;

    public Order execute(String id, UpdateOrderStatusDto data) {
        log.info("Solicitud de cambio de estado para la orden ID: {}. Nuevo estado propuesto: {}", id, data.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fallo al actualizar estado: La orden con ID {} no existe.", id);
                    return new NoSuchElementException("No se encontró la orden con ID: " + id);
                });

        log.info("Transición de estado para Orden {}: [{}] -> [{}]",
                order.getOrderCode(), order.getStatus(), data.getStatus());

        try {
            String oldStatus = order.getStatus();
            order.setStatus(data.getStatus());
            Order updatedOrder = orderRepository.save(order);

            ordenProducer.sendToInformChangeInStatus(order.getId(), oldStatus, data.getStatus());
            log.info("Estado de la orden {} actualizado exitosamente en MongoDB.", updatedOrder.getOrderCode());
            return updatedOrder;

        } catch (Exception e) {
            log.error("Error crítico al actualizar el estado de la orden {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error técnico al procesar el cambio de estado", e);
        }
    }
}