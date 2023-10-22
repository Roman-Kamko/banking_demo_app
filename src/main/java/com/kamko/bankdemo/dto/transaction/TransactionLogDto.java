package com.kamko.bankdemo.dto.transaction;

import com.kamko.bankdemo.entity.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionLogDto(

        @Schema(description = "type of operation")
        Operation operation,

        @Schema(description = "transaction amount")
        BigDecimal amount,

        @Schema(description = "transaction date and time")
        LocalDateTime dateTime

) {
}
