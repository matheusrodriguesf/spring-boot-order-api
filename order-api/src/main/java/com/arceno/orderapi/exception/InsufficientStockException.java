package com.arceno.orderapi.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, Integer available, Integer requested) {
        super("Estoque insuficiente para o produto " + productId
                + ". Disponível: " + available + ", solicitado: " + requested);
    }
}
