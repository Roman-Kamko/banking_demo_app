package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.transaction.TransactionLogDto;
import com.kamko.bankdemo.entity.Operation;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.service.impl.TransactionLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionLogController.class)
class TransactionLogControllerTest {

    @MockBean
    private TransactionLogServiceImpl transactionLogService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAccountTransactions_success() throws Exception {
        var dateTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        var content = List.of(
                new TransactionLogDto(Operation.DEPOSIT, BigDecimal.TEN, dateTime),
                new TransactionLogDto(Operation.WITHDRAW, BigDecimal.TEN, dateTime)
        );
        var pageable = PageRequest.of(0, 2);
        var page = new PageImpl<>(content, pageable, content.size());
        doReturn(page).when(transactionLogService).findAccountTransactions(anyLong(), anyInt(), anyInt());
        mockMvc.perform(get("/api/v1/transaction-logs/1")
                        .param("pageNumber", "0")
                        .param("pageSize", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].operation").value(Operation.DEPOSIT.toString()),
                        jsonPath("$.content[0].amount").value(BigDecimal.TEN),
                        jsonPath("$.content[0].dateTime").value(dateTime.format(ISO_LOCAL_DATE_TIME)),
                        jsonPath("$.content[1].operation").value(Operation.WITHDRAW.toString()),
                        jsonPath("$.content[1].amount").value(BigDecimal.TEN),
                        jsonPath("$.content[1].dateTime").value(dateTime.format(ISO_LOCAL_DATE_TIME)),
                        jsonPath("$.metadata.page").value("0"),
                        jsonPath("$.metadata.size").value("2"),
                        jsonPath("$.metadata.totalElement").value("2")
                );
    }

    @Test
    void findAccountTransactions_accountNotFound() throws Exception {
        doThrow(AccountNotFoundException.class)
                .when(transactionLogService).findAccountTransactions(anyLong(), anyInt(), anyInt());
        mockMvc.perform(get("/api/v1/transaction-logs/1")
                        .param("pageNumber", "0")
                        .param("pageSize", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}