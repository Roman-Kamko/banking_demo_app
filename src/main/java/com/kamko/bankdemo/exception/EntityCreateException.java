package com.kamko.bankdemo.exception;

public class EntityCreateException extends RuntimeException{

    @Override
    public String getMessage() {
        return "There was an error creating an account";
    }

}
