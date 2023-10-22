package com.kamko.bankdemo.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@Schema(description = "Use to create a new account")
public record NewAccountDto(

        @NotBlank
        @Schema(description = "account name", example = "My first account")
        String name,

        @NotBlank
        @Pattern(regexp = "\\d{4}$", message = "incorrect PIN code entry to create account: ${validatedValue}")
        @Schema(description = "pin code", example = "1111", pattern = "\\d{4}$")
        String pin

) {
}
