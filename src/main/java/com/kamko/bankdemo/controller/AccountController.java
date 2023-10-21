package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.PageResponse;
import com.kamko.bankdemo.dto.account.CreateAccountDto;
import com.kamko.bankdemo.dto.account.CreatedAccountDto;
import com.kamko.bankdemo.dto.account.NameBalanceAccountDto;
import com.kamko.bankdemo.dto.request.DepositRequest;
import com.kamko.bankdemo.dto.request.TransferRequest;
import com.kamko.bankdemo.dto.request.WithdrawRequest;
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

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "get account by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = CreatedAccountDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<CreatedAccountDto> findOne(@PathVariable @Parameter(description = "account id", example = "1") Long id) {
        return ResponseEntity.ok(accountService.findOne(id));
    }

    @Operation(summary = "get page of accounts")
    @ApiResponse(responseCode = "200", description = "OK", content = {
            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
            @Schema(implementation = PageResponse.class))})
    @GetMapping
    public PageResponse<NameBalanceAccountDto> findAll(@RequestParam @Parameter(example = "0") Integer pageNumber,
                                                       @RequestParam @Parameter(example = "5") Integer pageSize) {
        return PageResponse.of(accountService.findAll(pageNumber, pageSize));
    }

    @Operation(summary = "create new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = CreatedAccountDto.class))})
    })
    @PostMapping
    public ResponseEntity<CreatedAccountDto> create(@RequestBody @Validated CreateAccountDto account) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.create(account));
    }

    @Operation(summary = "deposit funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = CreatedAccountDto.class))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @PutMapping("/deposit")
    public ResponseEntity<CreatedAccountDto> deposit(@RequestBody @Validated DepositRequest depositRequest) {
        return ResponseEntity.ok(accountService.deposit(depositRequest));
    }

    @Operation(summary = "withdraw funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = CreatedAccountDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "401", description = "Unauthorised", content = {
                    @Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @PutMapping("/withdraw")
    public ResponseEntity<CreatedAccountDto> withdraw(@RequestBody @Validated WithdrawRequest withdrawRequest) {
        return ResponseEntity.ok(accountService.withdraw(withdrawRequest));
    }

    @Operation(summary = "transfer funds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request completed", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = CreatedAccountDto.class))}),
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
    public ResponseEntity<CreatedAccountDto> transfer(@RequestBody @Validated TransferRequest transferRequest) {
        return ResponseEntity.ok(accountService.transfer(transferRequest));
    }

}
