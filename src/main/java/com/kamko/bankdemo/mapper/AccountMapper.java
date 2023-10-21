package com.kamko.bankdemo.mapper;

import com.kamko.bankdemo.dto.account.CreateAccountDto;
import com.kamko.bankdemo.dto.account.CreatedAccountDto;
import com.kamko.bankdemo.dto.account.NameBalanceAccountDto;
import com.kamko.bankdemo.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

    Account toEntity(CreateAccountDto accountDto);

    CreatedAccountDto toCreatedDto(Account account);

    NameBalanceAccountDto toNameBalanceDto(Account account);

}
