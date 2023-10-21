package com.kamko.bankdemo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(
        name = "Withdraw",
        description = "Use to make a withdraw"
)
public record WithdrawRequest(

        @NotNull
        @Positive
        @Schema(
                description = "Account ID for debiting funds",
                example = "1"
        )
        Long fromAccountId,

        @NotNull
        @Positive
        @Schema(
                description = "Replenishment amount",
                example = "200"
        )
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "\\d{4}$")
        @Schema(
                description = "pin code",
                example = "1111"
        )
        String pin
) {
}
