package com.kamko.bankdemo.exception;

public class WrongPinException extends RuntimeException {

    private final Long id;

    public WrongPinException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Wrong pin for account with ID: %d".formatted(id);
    }
}
