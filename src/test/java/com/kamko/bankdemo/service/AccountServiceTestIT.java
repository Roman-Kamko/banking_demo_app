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
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional()
@Sql("classpath:script/data.sql")
class AccountServiceTestIT {

    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private AccountRepo accountRepo;

    @Test
    void findAll_successes() {
        List<AccountNameBalanceDto> expected = List.of(
                new AccountNameBalanceDto("first", BigDecimal.valueOf(1000).setScale(2, HALF_UP)),
                new AccountNameBalanceDto("second", BigDecimal.valueOf(500).setScale(2, HALF_UP))
        );
        int pageNumber = 0;
        int pageSize = 2;
        Page<AccountNameBalanceDto> actual = accountService.findAll(pageNumber, pageSize);
        assertThat(actual.getContent()).isNotEmpty().isEqualTo(expected);
    }

    @Test
    void create_successes() {
        NewAccountDto newAccount = new NewAccountDto("name", "1234");
        AccountIdNameBalanceDto actual = accountService.create(newAccount);
        AccountIdNameBalanceDto expected = new AccountIdNameBalanceDto(3L, "name", BigDecimal.ZERO);
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void deposit_successes() {
        DepositRequest depositRequest = new DepositRequest(1L, BigDecimal.valueOf(100));
        AccountIdNameBalanceDto actual = accountService.deposit(depositRequest);
        AccountIdNameBalanceDto expected = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(1_100).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void withdraw_successes() {
        WithdrawRequest withdrawRequest = new WithdrawRequest(1L, BigDecimal.valueOf(100), "1111");
        AccountIdNameBalanceDto actual = accountService.withdraw(withdrawRequest);
        AccountIdNameBalanceDto expected = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(900).setScale(2, HALF_UP));
        assertThat(actual).isNotNull().isEqualTo(expected);
    }

    @Test
    void transfer_successes() {
        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.valueOf(100), "1111");
        accountService.transfer(transferRequest);
        BigDecimal expectedBalanceFromAccount = BigDecimal.valueOf(900).setScale(2, HALF_UP);
        BigDecimal expectedBalanceToAccount = BigDecimal.valueOf(600).setScale(2, HALF_UP);
        BigDecimal actualBalanceFromAccount = accountRepo.findAll().get(0).getBalance();
        BigDecimal actualBalanceToAccount = accountRepo.findAll().get(1).getBalance();
        assertAll(
                () -> assertThat(actualBalanceFromAccount).isEqualTo(expectedBalanceFromAccount),
                () -> assertThat(actualBalanceToAccount).isEqualTo(expectedBalanceToAccount)
        );
    }

    @Test
    void accountNotFoundException() {
        Class<AccountNotFoundException> expectedException = AccountNotFoundException.class;
        long wrongId = 10L;
        DepositRequest wrongDepositRequest = new DepositRequest(wrongId, BigDecimal.TEN);
        WithdrawRequest wrongWithdrawRequest = new WithdrawRequest(wrongId, BigDecimal.TEN, "1111");
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.deposit(wrongDepositRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(wrongWithdrawRequest))
        );
    }

    @Test
    void idMatchingException() {
        Class<IdMatchingException> expectedException = IdMatchingException.class;
        TransferRequest wrongTransferRequest = new TransferRequest(1L, 1L, BigDecimal.TEN, "1111");
        assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(wrongTransferRequest));
    }

    @Test
    void notEnoughFundsException() {
        Class<NotEnoughFundsException> expectedException = NotEnoughFundsException.class;
        BigDecimal amount = BigDecimal.valueOf(10_000);
        WithdrawRequest withdrawRequest = new WithdrawRequest(1L, amount, "1111");
        TransferRequest transferRequest = new TransferRequest(1L, 2L, amount, "1111");
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(transferRequest))
        );

    }

    @Test
    void wrongPinException() {
        Class<WrongPinException> expectedException = WrongPinException.class;
        String wrongPin = "1112";
        WithdrawRequest withdrawRequest = new WithdrawRequest(1L, BigDecimal.TEN, wrongPin);
        TransferRequest transferRequest = new TransferRequest(1L, 2L, BigDecimal.TEN, wrongPin);
        assertAll(
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.withdraw(withdrawRequest)),
                () -> assertThatExceptionOfType(expectedException).isThrownBy(() -> accountService.transfer(transferRequest))
        );
    }

}