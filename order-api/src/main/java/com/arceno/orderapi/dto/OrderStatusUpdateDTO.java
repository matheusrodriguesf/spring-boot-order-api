package com.arceno.orderapi.dto;

import com.arceno.orderapi.enumeration.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(
        @NotNull(message = "Status é obrigatório")
        OrderStatus status) {
}
