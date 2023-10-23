package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.EntityConvertingException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.mapper.AccountMapper;
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.service.impl.AccountServiceImpl;
import com.kamko.bankdemo.service.impl.SecurityServiceImpl;
import com.kamko.bankdemo.service.impl.TransactionLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static com.kamko.bankdemo.data.PreparedData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepo accountRepo;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private SecurityServiceImpl securityService;
    @Mock
    private TransactionLogServiceImpl transactionService;
    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void findOne_success() {
        doReturn(Optional.of(ACCOUNT)).when(accountRepo).findById(anyLong());
        doReturn(ACCOUNT_RESPONSE).when(accountMapper).toIdNameBalance(any(Account.class));
        var actual = accountService.findOne(1L);
        assertAll(
                () -> assertThat(actual).isNotNull().isEqualTo(ACCOUNT_RESPONSE),
                () -> verify(accountRepo, only()).findById(anyLong()),
                () -> verify(accountMapper, only()).toIdNameBalance(any(Account.class))
        );
    }

    @Test
    void findAll_success() {
        doReturn(new PageImpl<>(PAGE_CONTENT)).when(accountRepo).findAll(any(Pageable.class));
        doReturn(NAME_BALANCE_ACCOUNT_RESPONSE).when(accountMapper).toNameBalance(any(Account.class));
        var actualResult = accountService.findAll(0, 2);
        assertAll(
                () -> assertThat(actualResult.getContent()).isNotEmpty().containsExactly(NAME_BALANCE_ACCOUNT_RESPONSE),
                () -> assertThat(actualResult.getTotalElements()).isEqualTo(1),
                () -> verify(accountRepo, only()).findAll(any(Pageable.class)),
                () -> verify(accountMapper, only()).toNameBalance(any(Account.class))
        );
    }

    @Test
    void create_success() {
        doReturn(ACCOUNT).when(accountMapper).toEntity(any(NewAccountDto.class));
        doReturn("1111").when(securityService).encode(anyString());
        doReturn(ACCOUNT).when(accountRepo).save(any(Account.class));
        doReturn(ACCOUNT_RESPONSE).when(accountMapper).toIdNameBalance(any(Account.class));
        var accountRequest = new NewAccountDto("first", "1111");
        assertAll(
                () -> assertThat(accountService.create(accountRequest)).isEqualTo(ACCOUNT_RESPONSE),
                () -> verify(accountMapper, times(1)).toEntity(accountRequest),
                () -> verify(securityService, only()).encode(anyString()),
                () -> verify(accountRepo, only()).save(any(Account.class)),
                () -> verify(accountMapper, times(1)).toIdNameBalance(any(Account.class))
        );
    }

    @Test
    void accountNotFoundException() {
        doReturn(Optional.empty()).when(accountRepo).findById(anyLong());
        assertAll(
                () -> assertThatExceptionOfType(AccountNotFoundException.class)
                        .isThrownBy(() -> accountService.findOne(1L)),
                () -> assertThatExceptionOfType(AccountNotFoundException.class)
                        .isThrownBy(() -> accountService.deposit(DEPOSIT_REQUEST)),
                () -> assertThatExceptionOfType(AccountNotFoundException.class)
                        .isThrownBy(() -> accountService.withdraw(WITHDRAW_REQUEST))
        );
    }

    @Test
    void deposit_success() {
        doReturn(Optional.of(ACCOUNT)).when(accountRepo).findById(anyLong());
        doReturn(ACCOUNT).when(accountRepo).saveAndFlush(any(Account.class));
        var result =
                new AccountIdNameBalanceDto(ACCOUNT.getId(), ACCOUNT.getName(),
                        ACCOUNT.getBalance().add(DEPOSIT_REQUEST.amount()));
        doReturn(result).when(accountMapper).toIdNameBalance(any(Account.class));
        assertAll(
                () -> assertThat(accountService.deposit(DEPOSIT_REQUEST)).isEqualTo(result),
                () -> verify(accountRepo, times(1)).findById(anyLong()),
                () -> verify(accountRepo, times(1)).saveAndFlush(any(Account.class)),
                () -> verify(accountMapper, only()).toIdNameBalance(any(Account.class))
        );
    }

    @Test
    void withdraw_success() {
        doReturn(Optional.of(ACCOUNT)).when(accountRepo).findById(anyLong());
        doReturn(ACCOUNT).when(accountRepo).saveAndFlush(any(Account.class));
        var result =
                new AccountIdNameBalanceDto(ACCOUNT.getId(), ACCOUNT.getName(),
                        ACCOUNT.getBalance().subtract(WITHDRAW_REQUEST.amount()));
        doReturn(result).when(accountMapper).toIdNameBalance(any(Account.class));
        assertAll(
                () -> assertThat(accountService.withdraw(WITHDRAW_REQUEST)).isEqualTo(result),
                () -> verify(accountRepo, times(1)).findById(anyLong()),
                () -> verify(securityService, only()).verifyPin(anyString(), anyString(), anyLong()),
                () -> verify(accountRepo, times(1)).saveAndFlush(any(Account.class)),
                () -> verify(accountMapper, only()).toIdNameBalance(any(Account.class))
        );
    }

    @Test
    void idMatchingException() {
        var wrongTransferRequest =
                new TransferRequest(ACCOUNT.getId(), ACCOUNT.getId(), BigDecimal.TEN, ACCOUNT.getPin());
        assertThatExceptionOfType(IdMatchingException.class)
                .isThrownBy(() -> accountService.transfer(wrongTransferRequest));
    }

    @Test
    void notEnoughFundsException() {
        doReturn(Optional.of(ACCOUNT)).when(accountRepo).findById(anyLong());
        assertThatExceptionOfType(NotEnoughFundsException.class)
                .isThrownBy(() -> accountService.withdraw(WRONG_AMOUNT_WITHDRAW_REQUEST));
    }

    @Test
    void entityCreateException() {
        doReturn(null).when(accountMapper).toEntity(any(NewAccountDto.class));
        assertThatExceptionOfType(EntityConvertingException.class).isThrownBy(() -> accountService.create(new NewAccountDto("qwe", "1111")));
    }

}