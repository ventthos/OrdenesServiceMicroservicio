package com.ordenes.ordenservice.controller;

import com.ordenes.ordenservice.dto.CreateOrderDto;
import com.ordenes.ordenservice.dto.UpdateOrderDto;
import com.ordenes.ordenservice.dto.UpdateOrderStatusDto;
import com.ordenes.ordenservice.models.Order;
import com.ordenes.ordenservice.response.GeneralResponse;
import com.ordenes.ordenservice.service.*;
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
    private final UpdateOrderService updateOrderService;
    private final CheckProductInOrdersService checkProductInOrdersService;
    private final GetAllOrdersService getAllOrdersService;

    // 7. OBTENER TODAS LAS ORDENES (GET)
    @GetMapping
    public ResponseEntity<GeneralResponse<List<Order>>> getAllOrders() {
        List<Order> orders = getAllOrdersService.execute();
        return ResponseEntity.ok(GeneralResponse.<List<Order>>builder()
                .status("SUCCESS")
                .message("Todas las órdenes recuperadas")
                .data(orders)
                .build());
    }

    // 6. VERIFICAR SI UN PRODUCTO ESTA EN UNA ORDEN (GET)
    @GetMapping("/exists-product/{productId}")
    public ResponseEntity<Boolean> existsProductInOrders(@PathVariable String productId) {
        boolean exists = checkProductInOrdersService.execute(productId);
        return ResponseEntity.ok(exists);
    }

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

    // 5. ACTUALIZAR ORDEN (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<Order>> updateOrder(
            @PathVariable String id,
            @RequestBody UpdateOrderDto dto) {

        Order orderActualizada = updateOrderService.execute(id, dto);
        return ResponseEntity.ok(GeneralResponse.<Order>builder()
                .status("SUCCESS")
                .message("Orden actualizada correctamente")
                .data(orderActualizada)
                .build());
    }
}