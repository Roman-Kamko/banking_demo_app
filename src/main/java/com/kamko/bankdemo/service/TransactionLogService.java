package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.transaction.TransactionLogDto;
import com.kamko.bankdemo.entity.Account;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface TransactionLogService {

    void logDeposit(Account account, BigDecimal amount);

    void logWithdraw(Account account, BigDecimal amount);

    Page<TransactionLogDto> findAccountTransactions(Long accountId, Integer pageNum, Integer pageSize);

}
