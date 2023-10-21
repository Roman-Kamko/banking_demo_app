package com.kamko.bankdemo.exception;

import com.kamko.bankdemo.entity.Account;

import java.math.BigDecimal;

public class NotEnoughFundsException extends RuntimeException {

    public NotEnoughFundsException(Account account, BigDecimal amount) {
        super(
                "There are not enough funds in the account with ID: %d. Current balance: %s. Attempt to withdraw: %s"
                        .formatted(account.getId(), account.getBalance(), amount)
        );
    }

}
