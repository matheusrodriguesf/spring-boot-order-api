package com.arceno.orderapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arceno.orderapi.dto.OrderCreateRequestDTO;
import com.arceno.orderapi.dto.OrderResponseDTO;
import com.arceno.orderapi.service.OrderService;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderCreateRequestDTO orderCreateRequestDTO) {
        var createdOrder = orderService.createOrder(orderCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

}
