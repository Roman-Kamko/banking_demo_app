package com.kamko.bankdemo.service;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import org.springframework.data.domain.Page;

public interface AccountService {

    AccountIdNameBalanceDto findOne(Long id);

    Page<AccountNameBalanceDto> findAll(Integer pageNum, Integer pageSize);

    AccountIdNameBalanceDto create(NewAccountDto newAccountDto);

    AccountIdNameBalanceDto deposit(DepositRequest depositRequest);

    AccountIdNameBalanceDto withdraw(WithdrawRequest withdrawRequest);

    void transfer(TransferRequest transferRequest);

}
