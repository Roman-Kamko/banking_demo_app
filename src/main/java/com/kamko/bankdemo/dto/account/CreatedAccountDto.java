package com.kamko.bankdemo.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "Created account", description = "Use to see base info about account")
public record CreatedAccountDto(

        @Schema(description = "account id", example = "1")
        Long id,

        @Schema(description = "account name", example = "My first account")
        String name,

        @Schema(description = "account balance", example = "100.00")
        BigDecimal balance

) {
}
