package com.kamko.bankdemo.mapper;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    Account toEntity(NewAccountDto accountRequest);

    AccountIdNameBalanceDto toIdNameBalance(Account account);

    AccountNameBalanceDto toNameBalance(Account account);

    DepositRequest toDeposit(TransferRequest transferRequest);

    WithdrawRequest toWithdraw(TransferRequest transferRequest);

}
