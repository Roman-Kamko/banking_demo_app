package com.kamko.bankdemo.service.impl;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.EntityConvertingException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.mapper.AccountMapper;
import com.kamko.bankdemo.repo.AccountRepo;
import com.kamko.bankdemo.service.AccountService;
import com.kamko.bankdemo.service.SecurityService;
import com.kamko.bankdemo.service.TransactionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static java.math.RoundingMode.HALF_UP;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    private final SecurityService securityService;
    private final TransactionLogService transactionService;
    private static final int ROUNDING_SCALE = 2;

    @Override
    public Page<AccountNameBalanceDto> findAll(Integer pageNum, Integer pageSize) {
        return accountRepo.findAll(PageRequest.of(pageNum, pageSize))
                .map(accountMapper::toNameBalance);
    }

    @Override
    @Transactional
    public AccountIdNameBalanceDto create(NewAccountDto newAccountDto) {
        return Optional.of(newAccountDto)
                .map(accountMapper::toEntity)
                .map(account -> {
                    account.setPin(securityService.encode(newAccountDto.pin()));
                    return accountRepo.save(account);
                })
                .map(accountMapper::toIdNameBalance)
                .orElseThrow(() -> new EntityConvertingException(newAccountDto));
    }

    @Override
    @Transactional
    public AccountIdNameBalanceDto deposit(DepositRequest depositRequest) {
        Long accountId = depositRequest.toAccountId();
        BigDecimal amount = depositRequest.amount();
        return accountRepo.findById(accountId)
                .map(account -> {
                    account.setBalance(increaseBalance(account, amount));
                    accountRepo.saveAndFlush(account);
                    transactionService.logDeposit(account, amount);
                    return accountMapper.toIdNameBalance(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public AccountIdNameBalanceDto withdraw(WithdrawRequest withdrawRequest) {
        Long accountId = withdrawRequest.fromAccountId();
        BigDecimal amount = withdrawRequest.amount();
        return accountRepo.findById(accountId)
                .map(account -> {
                    securityService.verifyPin(withdrawRequest.pin(), account.getPin(), accountId);
                    account.setBalance(reduceBalance(account, amount));
                    accountRepo.saveAndFlush(account);
                    transactionService.logWithdraw(account, amount);
                    return accountMapper.toIdNameBalance(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    @Transactional
    public void transfer(TransferRequest transferRequest) {
        Long fromAccountId = transferRequest.fromAccountId();
        Long toAccountId = transferRequest.toAccountId();
        if (Objects.equals(fromAccountId, toAccountId)) {
            throw new IdMatchingException(fromAccountId);
        }
        withdraw(accountMapper.toWithdraw(transferRequest));
        deposit(accountMapper.toDeposit(transferRequest));
    }

    private BigDecimal increaseBalance(Account account, BigDecimal amount) {
        return account.getBalance()
                .add(amount)
                .setScale(ROUNDING_SCALE, HALF_UP);
    }

    private BigDecimal reduceBalance(Account account, BigDecimal amount) {
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughFundsException(account, amount);
        }
        return currentBalance.subtract(amount)
                .setScale(ROUNDING_SCALE, HALF_UP);
    }

}

