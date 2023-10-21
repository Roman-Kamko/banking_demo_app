package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.transaction.TransactionDto;
import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.entity.Operation;
import com.kamko.bankdemo.entity.Transaction;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.mapper.TransactionMapper;
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.repo.TransactionRepo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final TransactionMapper transactionMapper;

    @Transactional
    public void logDeposit(Account account, BigDecimal amount) {
        var transaction = new Transaction(Operation.DEPOSIT, amount, account);
        transactionRepo.save(transaction);
    }

    @Transactional
    public void logWithdraw(Account account, BigDecimal amount) {
        var transaction = new Transaction(Operation.WITHDRAW, amount, account);
        transactionRepo.save(transaction);
    }

    public Page<TransactionDto> findAccountTransactions(Long accountId, Integer pageNum, Integer pageSize) {
        if (!accountRepo.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }
        return transactionRepo.findPageOfTransaction(PageRequest.of(pageNum, pageSize), accountId)
                .map(transactionMapper::toDto);
    }

}
