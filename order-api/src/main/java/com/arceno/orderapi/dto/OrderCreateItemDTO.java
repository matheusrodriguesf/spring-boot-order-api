package com.arceno.orderapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderCreateItemDTO(
        @NotNull(message = "Id do produto é obrigatório")
        @Positive(message = "Id do produto deve ser maior que zero")
        Long productId,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity) {
}
