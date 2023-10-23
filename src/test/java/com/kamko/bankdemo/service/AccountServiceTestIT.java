package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.exception.WrongPinException;
import com.kamko.bankdemo.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
@Sql("classpath:script/data.sql")
class AccountServiceTestIT {

    @Autowired
    private AccountServiceImpl accountService;

    @Test
    void findById_success() {
        var expected = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(1000).setScale(2, HALF_UP));
        var actual = accountService.findOne(1L);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void findAll_successes() {
        var expected = List.of(
                new AccountNameBalanceDto("first", BigDecimal.valueOf(1000).setScale(2, HALF_UP)),
                new AccountNameBalanceDto("second", BigDecimal.valueOf(500).setScale(2, HALF_UP))
        );
        var pageNumber = 0;
        var pageSize = 2;
        var actual = accountService.findAll(pageNumber, pageSize);
        assertThat(actual.getContent()).isNotEmpty().isEqualTo(expected);
    }

    @Test
    void create_successes() {
        var newAccount = new NewAccountDto("name", "1234");
        var actual = accountService.create(newAccount);
        var expected = new AccountIdNameBalanceDto(3L, "name", BigDecimal.ZERO);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void deposit_successes() {
        var depositRequest = new DepositRequest(1L, BigDecimal.valueOf(100));
        var actual = accountService.deposit(depositRequest);
        var expected = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(1_100).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void withdraw_successes() {
        var withdrawRequest = new WithdrawRequest(1L, BigDecimal.valueOf(100), "1111");
        var actual = accountService.withdraw(withdrawRequest);
        var expected = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(900).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void transfer_successes() {
        var transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(100), "1111");
        accountService.transfer(transferRequest);
        var expectedBalanceFromAccount = BigDecimal.valueOf(900).setScale(2, HALF_UP);
        var expectedBalanceToAccount = BigDecimal.valueOf(600).setScale(2, HALF_UP);
        var actualBalanceFromAccount = accountService.findOne(1L).balance();
        var actualBalanceToAccount = accountService.findOne(2L).balance();
        assertAll(
                () -> assertThat(actualBalanceFromAccount).isEqualTo(expectedBalanceFromAccount),
                () -> assertThat(actualBalanceToAccount).isEqualTo(expectedBalanceToAccount)
        );
    }

    @Test
    void accountNotFoundException() {
        var expectedException = AccountNotFoundException.class;
        var wrongId = 10L;
        var wrongDepositRequest = new DepositRequest(wrongId, BigDecimal.TEN);
        var wrongWithdrawRequest = new WithdrawRequest(wrongId, BigDecimal.TEN, "1111");
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.findOne(wrongId)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.deposit(wrongDepositRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(wrongWithdrawRequest))
        );
    }

    @Test
    void idMatchingException() {
        var expectedException = IdMatchingException.class;
        var wrongTransferRequest = new TransferRequest(1L, 1L, BigDecimal.TEN, "1111");
        assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(wrongTransferRequest));
    }

    @Test
    void notEnoughFundsException() {
        var expectedException = NotEnoughFundsException.class;
        var amount = BigDecimal.valueOf(10_000);
        var withdrawRequest = new WithdrawRequest(1L, amount, "1111");
        var transferRequest = new TransferRequest(1L, 2L, amount, "1111");
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(transferRequest))
        );

    }

    @Test
    void wrongPinException() {
        var expectedException = WrongPinException.class;
        var wrongPin = "1112";
        var withdrawRequest = new WithdrawRequest(1L, BigDecimal.TEN, wrongPin);
        var transferRequest = new TransferRequest(1L, 2L, BigDecimal.TEN, wrongPin);
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(transferRequest))
        );
    }

}