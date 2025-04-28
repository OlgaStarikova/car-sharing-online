package com.example.carsharingonline.exception;

public class CarNotAvailableException extends RuntimeException {
    public CarNotAvailableException(String message) {
        super(message);
    }
}
