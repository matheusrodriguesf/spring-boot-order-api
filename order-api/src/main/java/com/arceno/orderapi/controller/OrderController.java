package com.arceno.orderapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.arceno.orderapi.dto.OrderCreateRequestDTO;
import com.arceno.orderapi.dto.OrderResponseDTO;
import com.arceno.orderapi.dto.OrderStatusUpdateDTO;
import com.arceno.orderapi.dto.OrderUpdateRequestDTO;
import com.arceno.orderapi.enumeration.OrderStatus;
import com.arceno.orderapi.service.OrderService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@Validated
@RequestMapping("/orders")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderController {

    OrderService orderService;

    @GetMapping
    public Page<OrderResponseDTO> allOrders(Pageable pageable,
            @RequestParam(required = false) OrderStatus status) {
        return orderService.allOrders(pageable, status);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getOrderById(@PathVariable @Positive(message = "Id deve ser maior que zero") Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateRequestDTO orderCreateRequestDTO) {
        var createdOrder = orderService.createOrder(orderCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    public OrderResponseDTO updateOrder(
            @PathVariable @Positive(message = "Id deve ser maior que zero") Long id,
            @RequestBody @Valid OrderUpdateRequestDTO orderUpdateRequestDTO) {
        return orderService.updateOrder(id, orderUpdateRequestDTO);
    }

    @PutMapping("/{id}/status")
    public OrderResponseDTO updateOrderStatus(
            @PathVariable @Positive(message = "Id deve ser maior que zero") Long id,
            @RequestBody @Valid OrderStatusUpdateDTO orderStatusUpdateDTO) {
        return orderService.updateOrderStatus(id, orderStatusUpdateDTO.status());
    }

}
