package com.kamko.bankdemo.exception;

import com.kamko.bankdemo.dto.account.NewAccountDto;

public class EntityConvertingException extends RuntimeException {
    public EntityConvertingException(NewAccountDto newAccountDto) {
        super("entity conversion error: " + newAccountDto);
    }

}
