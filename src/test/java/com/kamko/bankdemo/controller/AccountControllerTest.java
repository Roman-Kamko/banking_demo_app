package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account.NewAccountDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.TransferRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.exception.WrongPinException;
import com.kamko.bankdemo.service.impl.AccountServiceImpl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    private static final String BASE_PATH = "/api/v1/accounts";
    private static JSONObject DEPOSIT_REQUEST;
    private static JSONObject WITHDRAW_REQUEST;
    private static JSONObject TRANSFER_REQUEST;

    @BeforeAll
    static void beforeAll() {
        DEPOSIT_REQUEST = new JSONObject(new HashMap<>(Map.of(
                "toAccountId", 1L,
                "amount", BigDecimal.TEN
        )));
        WITHDRAW_REQUEST = new JSONObject(new HashMap<>(Map.of(
                "fromAccountId", 1L,
                "amount", BigDecimal.TEN,
                "pin", "1111"
        )));
        TRANSFER_REQUEST = new JSONObject(new HashMap<>(Map.of(
                "fromAccountId", 1L,
                "toAccountId", 1L,
                "amount", BigDecimal.TEN,
                "pin", "1111"
        )));
    }

    @Test
    void findAll_success() throws Exception {
        List<AccountNameBalanceDto> content = List.of(
                new AccountNameBalanceDto("first", BigDecimal.valueOf(1000)),
                new AccountNameBalanceDto("second", BigDecimal.valueOf(500))
        );
        PageRequest pageable = PageRequest.of(0, 2);
        PageImpl<AccountNameBalanceDto> page = new PageImpl<>(content, pageable, content.size());
        doReturn(page).when(accountService).findAll(anyInt(), anyInt());
        mockMvc.perform(get(BASE_PATH)
                        .param("pageNumber", "0")
                        .param("pageSize", "2")
                        .accept(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.content[0].name").value("first"),
                        jsonPath("$.content[0].balance").value("1000"),
                        jsonPath("$.content[1].name").value("second"),
                        jsonPath("$.content[1].balance").value("500"),
                        jsonPath("$.metadata.page").value("0"),
                        jsonPath("$.metadata.size").value("2"),
                        jsonPath("$.metadata.totalElement").value("2")
                );
    }

    @Test
    void create_success() throws Exception {
        AccountIdNameBalanceDto response = new AccountIdNameBalanceDto(1L, "first", BigDecimal.ZERO);
        doReturn(response).when(accountService).create(any(NewAccountDto.class));
        JSONObject request = new JSONObject(new HashMap<>(Map.of(
                "name", "first",
                "pin", "1111"
        )));
        mockMvc.perform(post(BASE_PATH)
                        .content(request.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("first"),
                        jsonPath("$.balance").value(0)
                );
    }

    @Test
    void deposit_success() throws Exception {
        AccountIdNameBalanceDto response = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(90));
        doReturn(response).when(accountService).deposit(any(DepositRequest.class));
        mockMvc.perform(put(BASE_PATH + "/deposit")
                        .content(DEPOSIT_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("first"),
                        jsonPath("$.balance").value(BigDecimal.valueOf(90))
                );
    }

    @Test
    void deposit_accountNotFoundException() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).deposit(any(DepositRequest.class));
        mockMvc.perform(put(BASE_PATH + "/deposit")
                        .content(DEPOSIT_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void withdraw_success() throws Exception {
        AccountIdNameBalanceDto response = new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(90));
        doReturn(response).when(accountService).withdraw(any(WithdrawRequest.class));
        mockMvc.perform(put(BASE_PATH + "/withdraw")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("first"),
                        jsonPath("$.balance").value(BigDecimal.valueOf(90))
                );
    }

    @Test
    void withdraw_accountNotFoundException() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).withdraw(any(WithdrawRequest.class));
        mockMvc.perform(put(BASE_PATH + "/withdraw")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void withdraw_notEnoughFundsException() throws Exception {
        doThrow(NotEnoughFundsException.class).when(accountService).withdraw(any(WithdrawRequest.class));
        mockMvc.perform(put(BASE_PATH + "/withdraw")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_wrongPinException() throws Exception {
        doThrow(WrongPinException.class).when(accountService).withdraw(any(WithdrawRequest.class));
        mockMvc.perform(put(BASE_PATH + "/withdraw")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void transfer_success() throws Exception {
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void transfer_idMatchingException() throws Exception {
        doThrow(IdMatchingException.class).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

}