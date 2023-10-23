package com.kamko.bankdemo.service;

import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.entity.Operation;
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.repo.TransactionLogRepo;
import com.kamko.bankdemo.service.impl.TransactionLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class TransactionLogServiceTestIT {

    @Autowired
    private TransactionLogServiceImpl transactionLogService;
    @Autowired
    private TransactionLogRepo transactionRepo;
    @Autowired
    private AccountRepo accountRepo;

    @Test
    public void logDeposit_success() {
        var account = createTestAccount();
        var amount = BigDecimal.valueOf(100);
        accountRepo.saveAndFlush(account);
        transactionLogService.logDeposit(account, amount);
        assertThat(transactionRepo.findAll()).hasSize(1);
        var savedTransaction = transactionRepo.findById(1L);
        savedTransaction.ifPresent(transactionLog ->
                assertAll(
                        () -> assertThat(transactionLog.getOperation()).isEqualTo(Operation.DEPOSIT),
                        () -> assertThat(transactionLog.getAmount()).isEqualTo(amount),
                        () -> assertThat(transactionLog.getAccount()).isEqualTo(account)
                )
        );
    }

    @Test
    public void logWithdraw_success() {
        var account = createTestAccount();
        var amount = BigDecimal.valueOf(50);
        accountRepo.saveAndFlush(account);
        transactionLogService.logWithdraw(account, amount);
        assertThat(transactionRepo.findAll()).hasSize(1);
        var savedTransaction = transactionRepo.findById(1L);
        savedTransaction.ifPresent(transactionLog ->
                assertAll(
                        () -> assertThat(transactionLog.getOperation()).isEqualTo(Operation.WITHDRAW),
                        () -> assertThat(transactionLog.getAmount()).isEqualTo(amount),
                        () -> assertThat(transactionLog.getAccount()).isEqualTo(account)
                )
        );
    }

    @Test
    public void findAccountTransactions_success() {
        var account = createTestAccount();
        var depositAmount = BigDecimal.valueOf(100);
        var withdrawAmount = BigDecimal.valueOf(50);

        accountRepo.saveAndFlush(account);
        transactionLogService.logDeposit(account, depositAmount);
        transactionLogService.logWithdraw(account, withdrawAmount);

        var transactionLogPage = transactionLogService.findAccountTransactions(account.getId(), 0, 10);
        assertAll(
                () -> assertThat(transactionLogPage).isNotEmpty(),
                () -> assertThat(transactionLogPage).hasSize(2)
        );
    }

    private Account createTestAccount() {
        var account = new Account();
        account.setName("first");
        account.setBalance(BigDecimal.ZERO);
        return account;
    }

}