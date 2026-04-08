package com.arceno.orderapi.dto;

import java.math.BigDecimal;

public record ProductFormDTO(
        String name,
        String description,
        BigDecimal price,
        Integer stock) {

}
