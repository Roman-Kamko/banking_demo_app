package com.kamko.bankdemo.exception;

public class IdMatchingException extends RuntimeException {

    private final Long id;

    public IdMatchingException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "not a valid operation, the account with ID %d is trying to transfer funds to itself"
                .formatted(id);
    }
}
