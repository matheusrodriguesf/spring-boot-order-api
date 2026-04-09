package com.arceno.orderapi.exception;

import com.arceno.orderapi.enumeration.OrderStatus;

public class OrderUpdateNotAllowedException extends RuntimeException {

    public OrderUpdateNotAllowedException(Long orderId, OrderStatus status) {
        super("Não é permitido atualizar itens do pedido " + orderId + " com status " + status);
    }
}
