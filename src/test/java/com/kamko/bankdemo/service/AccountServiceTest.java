package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.account.CreateAccountDto;
import com.kamko.bankdemo.dto.account.CreatedAccountDto;
import com.kamko.bankdemo.dto.account.NameBalanceAccountDto;
import com.kamko.bankdemo.dto.request.DepositRequest;
import com.kamko.bankdemo.dto.request.TransferRequest;
import com.kamko.bankdemo.dto.request.WithdrawRequest;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.exception.WrongPinException;
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
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    void findById_success() {
        var expected = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(1000).setScale(2, HALF_UP));
        var actual = accountService.findOne(1L);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void findAll_successes() {
        var expected = List.of(
                new NameBalanceAccountDto("first", BigDecimal.valueOf(1000).setScale(2, HALF_UP)),
                new NameBalanceAccountDto("second", BigDecimal.valueOf(500).setScale(2, HALF_UP))
        );
        var pageNumber = 0;
        var pageSize = 2;
        var actual = accountService.findAll(pageNumber, pageSize);
        assertThat(actual.getContent()).isNotEmpty().isEqualTo(expected);
    }

    @Test
    void create_successes() {
        var newAccount = new CreateAccountDto("name", "1234");
        var actual = accountService.create(newAccount);
        var expected = new CreatedAccountDto(3L, "name", BigDecimal.ZERO);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void deposit_successes() {
        var depositRequest = new DepositRequest(1L, BigDecimal.valueOf(100));
        var actual = accountService.deposit(depositRequest);
        var expected = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(1_100).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void withdraw_successes() {
        var withdrawRequest = new WithdrawRequest(1L, BigDecimal.valueOf(100), "1111");
        var actual = accountService.withdraw(withdrawRequest);
        var expected = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(900).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void transfer_successes() {
        var transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(100), "1111");
        var actual = accountService.transfer(transferRequest);
        var expected = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(900).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
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
        var withdrawRequest = new WithdrawRequest(1L, BigDecimal.valueOf(10_000), "1111");
        assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest));
    }

    @Test
    void wrongPinException() {
        var expectedException = WrongPinException.class;
        var withdrawRequest = new WithdrawRequest(1L, BigDecimal.TEN, "1112");
        assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest));
    }

}