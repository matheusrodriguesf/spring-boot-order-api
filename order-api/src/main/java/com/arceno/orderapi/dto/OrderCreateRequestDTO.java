package com.arceno.orderapi.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderCreateRequestDTO(
        @NotNull(message = "Id do usuário é obrigatório")
        @Positive(message = "Id do usuário deve ser maior que zero")
        Long userId,

        @NotEmpty(message = "Pedido deve conter ao menos um item")
        List<@Valid OrderCreateItemDTO> items) {
}
