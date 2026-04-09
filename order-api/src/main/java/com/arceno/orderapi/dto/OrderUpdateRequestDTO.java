package com.arceno.orderapi.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OrderUpdateRequestDTO(
        @NotEmpty(message = "Pedido deve conter ao menos um item")
        List<@Valid OrderCreateItemDTO> items) {
}
