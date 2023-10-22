package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.mapper.AccountMapper;
import com.kamko.bankdemo.repo.AccountRepo;
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
public class AccountService {

    private final AccountRepo accountRepo;
    private final AccountMapper accountMapper;
    private final SecurityService securityService;
    private final TransactionLogService transactionService;
    private static final int ROUNDING_SCALE = 2;

    public AccountIdNameBalanceDto findOne(Long id) {
        return accountRepo.findById(id)
                .map(accountMapper::toIdNameBalance)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Page<AccountNameBalanceDto> findAll(Integer pageNum, Integer pageSize) {
        return accountRepo.findAll(PageRequest.of(pageNum, pageSize))
                .map(accountMapper::toNameBalance);
    }

    @Transactional
    public AccountIdNameBalanceDto create(NewAccountDto newAccountDto) {
        return Optional.of(newAccountDto)
                .map(accountMapper::toEntity)
                .map(account -> {
                    account.setPin(securityService.encode(newAccountDto.pin()));
                    account.setBalance(BigDecimal.ZERO);
                    return accountRepo.save(account);
                })
                .map(accountMapper::toIdNameBalance)
                .orElseThrow();
    }

    @Transactional
    public AccountIdNameBalanceDto deposit(DepositRequest depositRequest) {
        var accountId = depositRequest.toAccountId();
        var amount = depositRequest.amount();
        return accountRepo.findById(accountId)
                .map(account -> {
                    account.setBalance(increaseBalance(account, amount));
                    return accountRepo.saveAndFlush(account);
                })
                .map(account -> {
                    transactionService.logDeposit(account, amount);
                    return accountMapper.toIdNameBalance(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public AccountIdNameBalanceDto withdraw(WithdrawRequest withdrawRequest) {
        var accountId = withdrawRequest.fromAccountId();
        var amount = withdrawRequest.amount();
        return accountRepo.findById(accountId)
                .map(account -> {
                    securityService.verifyPin(withdrawRequest.pin(), account.getPin(), accountId);
                    account.setBalance(reduceBalance(account, amount));
                    return accountRepo.saveAndFlush(account);
                })
                .map(account -> {
                    transactionService.logWithdraw(account, amount);
                    return accountMapper.toIdNameBalance(account);
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public void transfer(TransferRequest transferRequest) {
        var fromAccountId = transferRequest.fromAccountId();
        var toAccountId = transferRequest.toAccountId();
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
        var currentBalance = account.getBalance();
        if (currentBalance.compareTo(amount) < 0) {
            throw new NotEnoughFundsException(account, amount);
        }
        return currentBalance.subtract(amount)
                .setScale(ROUNDING_SCALE, HALF_UP);
    }

}

