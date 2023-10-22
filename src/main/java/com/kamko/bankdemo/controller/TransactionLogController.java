package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.PageResponse;
import com.kamko.bankdemo.dto.transaction.TransactionLogDto;
import com.kamko.bankdemo.service.TransactionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/transactions")
@AllArgsConstructor
@Tag(name = "TransactionLog")
public class TransactionLogController {

    private final TransactionLogService transactionService;

    @Operation(summary = "get page of account transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = PageResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found", content = {
                    @Content(schema = @Schema(hidden = true))})
    })
    @GetMapping("/{accountId}")
    public PageResponse<TransactionLogDto> findAccountTransactions(@PathVariable @Parameter(example = "1") Long accountId,
                                                                   @Parameter(example = "0") Integer pageNumber,
                                                                   @Parameter(example = "0") Integer pageSize) {
        return PageResponse.of(transactionService.findAccountTransactions(accountId, pageNumber, pageSize));
    }

}
