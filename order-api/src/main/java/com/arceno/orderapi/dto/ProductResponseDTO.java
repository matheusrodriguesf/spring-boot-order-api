package com.arceno.orderapi.dto;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        String price,
        Integer stock) {
}
