package com.kamko.bankdemo.exception;

public class AccountNotFoundException extends RuntimeException {

    private final Long id;

    public AccountNotFoundException(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Account with id: %d not found".formatted(id);
    }

}
