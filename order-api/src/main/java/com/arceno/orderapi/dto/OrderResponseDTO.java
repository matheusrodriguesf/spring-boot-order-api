package com.arceno.orderapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        Long userId,
        String status,
        BigDecimal total,
        LocalDateTime createdAt,
        List<OrderItemResponseDTO> items) {
}
