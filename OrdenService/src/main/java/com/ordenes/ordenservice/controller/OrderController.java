package com.ordenes.ordenservice.controller;

import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.dto.UpdateOrderStatusDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.response.GeneralResponse;
import com.ordenes.ordenservice.service.CreateOrderService;
import com.ordenes.ordenservice.service.GetOrderByIdService;
import com.ordenes.ordenservice.service.GetOrdersByUserService;
import com.ordenes.ordenservice.service.UpdateOrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderService createOrderService;
    private final GetOrderByIdService getOrderByIdService;
    private final GetOrdersByUserService getOrdersByUserService;
    private final UpdateOrderStatusService updateOrderStatusService;

    // 1. CREAR ORDEN (POST)
    @PostMapping
    public ResponseEntity<GeneralResponse<Order>> createOrder(@RequestBody CreateOrderDto dto) {
        Order nuevaOrder = createOrderService.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GeneralResponse.<Order>builder()
                        .status("SUCCESS")
                        .message("Orden creada correctamente")
                        .data(nuevaOrder)
                        .build());
    }

    // 2. OBTENER POR ID DE MONGO (GET)
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<Order>> getOrderById(@PathVariable String id) {
        Order order = getOrderByIdService.execute(id);
        return ResponseEntity.ok(GeneralResponse.<Order>builder()
                .status("SUCCESS")
                .message("Orden recuperada con éxito")
                .data(order)
                .build());
    }

    // 3. OBTENER POR USUARIO (GET)
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<GeneralResponse<List<Order>>> getOrdersByUser(@PathVariable String userId) {
        List<Order> orders = getOrdersByUserService.execute(userId);
        return ResponseEntity.ok(GeneralResponse.<List<Order>>builder()
                .status("SUCCESS")
                .message("Órdenes del usuario recuperadas")
                .data(orders)
                .build());
    }

    // 4. ACTUALIZAR SOLO EL ESTATUS (PUT)
    @PutMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<Order>> updateOrderStatus(
            @PathVariable String id,
            @RequestBody UpdateOrderStatusDto dto) {

        Order orderActualizada = updateOrderStatusService.execute(id, dto);
        return ResponseEntity.ok(GeneralResponse.<Order>builder()
                .status("SUCCESS")
                .message("Estatus de la orden actualizado correctamente")
                .data(orderActualizada)
                .build());
    }
}