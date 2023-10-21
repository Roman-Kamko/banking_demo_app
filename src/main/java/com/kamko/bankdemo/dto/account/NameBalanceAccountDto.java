package com.kamko.bankdemo.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "Name balance account", description = "Use for PageResponse")
public record NameBalanceAccountDto(

        @Schema(description = "account name", example = "My first account")
        String name,
        @Schema(description = "account balance", example = "100.00")
        BigDecimal balance

) {
}
