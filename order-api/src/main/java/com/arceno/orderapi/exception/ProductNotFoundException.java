package com.arceno.orderapi.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Produto não encontrado com id: " + id);
    }

}
