package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.transaction.TransactionLogDto;
import com.kamko.bankdemo.entity.TransactionLog;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.mapper.TransactionLogMapper;
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.repo.TransactionLogRepo;
import com.kamko.bankdemo.service.impl.TransactionLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;

import static com.kamko.bankdemo.data.PreparedData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepo accountRepo;
    @Mock
    private TransactionLogMapper transactionMapper;
    @Mock
    private TransactionLogRepo transactionRepo;
    @InjectMocks
    private TransactionLogServiceImpl transactionService;

    @Test
    void logDeposit_success() {
        doReturn(DEPOSIT_TRANSACTION_LOG).when(transactionRepo).save(any(TransactionLog.class));
        transactionService.logDeposit(ACCOUNT, BigDecimal.TEN);
        verify(transactionRepo, only()).save(DEPOSIT_TRANSACTION_LOG);
    }

    @Test
    void logWithdraw_success() {
        doReturn(WITHDRAW_TRANSACTION_LOG).when(transactionRepo).save(any(TransactionLog.class));
        transactionService.logWithdraw(ACCOUNT, BigDecimal.TEN);
        verify(transactionRepo, only()).save(WITHDRAW_TRANSACTION_LOG);
    }

    @Test
    void findAccountTransactions_success() {
        doReturn(true).when(accountRepo).existsById(anyLong());
        PageImpl<TransactionLogDto> expected = new PageImpl<TransactionLogDto>(Collections.emptyList(), PageRequest.of(0, 2), 0);
        doReturn(expected).when(transactionRepo).findPageOfTransaction(any(Pageable.class), anyLong());
        Page<TransactionLogDto> actual = transactionService.findAccountTransactions(1L, 0, 2);
        assertAll(
                () -> verify(transactionRepo, only()).findPageOfTransaction(any(Pageable.class), anyLong()),
                () -> assertThat(actual).isNotNull().isEqualTo(expected)
        );
    }

    @Test
    void findAccountTransactions_accountNotFoundException() {
        doReturn(false).when(accountRepo).existsById(anyLong());
        assertAll(
                () -> assertThatExceptionOfType(AccountNotFoundException.class)
                        .isThrownBy(() -> transactionService.findAccountTransactions(1L, 0, 2)),
                () -> verify(transactionRepo, never()).findPageOfTransaction(any(Pageable.class), anyLong())
        );
    }

}