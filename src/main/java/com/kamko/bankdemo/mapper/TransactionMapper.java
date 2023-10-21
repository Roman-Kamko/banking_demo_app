package com.kamko.bankdemo.mapper;

import com.kamko.bankdemo.dto.transaction.TransactionDto;
import com.kamko.bankdemo.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    TransactionDto toDto(Transaction transaction);

}
