package com.arceno.orderapi.exception;

import com.arceno.orderapi.enumeration.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(Long orderId, OrderStatus from, OrderStatus to) {
        super("Transição de status inválida para pedido " + orderId + ": " + from + " -> " + to);
    }
}
