package com.arceno.orderapi.exception;

public class InvalidOrderRequestException extends RuntimeException {

    public InvalidOrderRequestException(String message) {
        super(message);
    }
}
