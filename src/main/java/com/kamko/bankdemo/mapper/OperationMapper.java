package com.kamko.bankdemo.mapper;

import com.kamko.bankdemo.dto.request.DepositRequest;
import com.kamko.bankdemo.dto.request.TransferRequest;
import com.kamko.bankdemo.dto.request.WithdrawRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {

    DepositRequest toDeposit(TransferRequest transferRequest);

    WithdrawRequest toWithdraw(TransferRequest transferRequest);

}
