package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.PageResponse;
import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "get page of accounts")
    @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = PageResponse.class))})
    @GetMapping
    public PageResponse<AccountNameBalanceDto> findAll(@RequestParam @Parameter(example = "0") Integer pageNumber,
                                                       @RequestParam @Parameter(example = "5") Integer pageSize) {
        return PageResponse.of(accountService.findAll(pageNumber, pageSize));
    }

    @Operation(summary = "create new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = AccountIdNameBalanceDto.class))})
    })
    @PostMapping
    public ResponseEntity<AccountIdNameBalanceDto> create(@RequestBody @Validated NewAccountDto account) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.create(account));
    }

    @Operation(summary = "deposit funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = AccountIdNameBalanceDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @PutMapping("/deposit")
    public AccountIdNameBalanceDto deposit(@RequestBody @Validated DepositRequest depositRequest) throws ExecutionException, InterruptedException {
        return accountService.deposit(depositRequest);
    }

    @Operation(summary = "withdraw funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = AccountIdNameBalanceDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @PutMapping("/withdraw")
    public AccountIdNameBalanceDto withdraw(@RequestBody @Validated WithdrawRequest withdrawRequest) {
        return accountService.withdraw(withdrawRequest);
    }

    @Operation(summary = "transfer funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "409", description = "Conflict", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @PutMapping("/transfer")
    public void transfer(@RequestBody @Validated TransferRequest transferRequest) {
        accountService.transfer(transferRequest);
    }

}
