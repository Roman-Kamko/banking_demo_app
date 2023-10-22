package com.kamko.bankdemo.dto.account_operation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Use to make a deposit")
public record DepositRequest(

        @NotNull
        @Positive
        @Schema(description = "id of the account being replenished", example = "1")
        Long toAccountId,

        @NotNull
        @Positive
        @Schema(description = "deposit amount", example = "200")
        BigDecimal amount
) {
}
