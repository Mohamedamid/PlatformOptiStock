package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.OrderRequestDTO;
import com.optistockplatrorm.dto.OrderResponseDTO;
import com.optistockplatrorm.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OptiResponse> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO order = orderService.create(dto);
        OptiResponse response = OptiResponse.builder()
                .message(order.message())
                .data(order)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/reserve")
    public ResponseEntity<OptiResponse> reserveOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.reserveOrder(id);
        OptiResponse response = OptiResponse.builder()
                .message("Order reserved successfully")
                .data(order)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OptiResponse> confirmOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.confirmOrder(id);
        OptiResponse response = OptiResponse.builder()
                .message("Order confirmed successfully")
                .data(order)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OptiResponse> cancelOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.cancel(id);
        OptiResponse response = OptiResponse.builder()
                .message("Order cancelled successfully")
                .data(order)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getOrder(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrder(id);
        OptiResponse response = OptiResponse.builder()
                .message("Order retrieved successfully")
                .data(order)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}