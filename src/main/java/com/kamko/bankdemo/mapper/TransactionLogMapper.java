package com.kamko.bankdemo.mapper;

import com.kamko.bankdemo.dto.transaction.TransactionLogDto;
import com.kamko.bankdemo.entity.TransactionLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionLogMapper {

    TransactionLogDto toDto(TransactionLog transaction);

}
