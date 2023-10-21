package com.kamko.bankdemo.controller;

import com.kamko.bankdemo.dto.account.CreateAccountDto;
import com.kamko.bankdemo.dto.account.CreatedAccountDto;
import com.kamko.bankdemo.dto.account.NameBalanceAccountDto;
import com.kamko.bankdemo.dto.request.DepositRequest;
import com.kamko.bankdemo.dto.request.TransferRequest;
import com.kamko.bankdemo.dto.request.WithdrawRequest;
import com.kamko.bankdemo.exception.AccountNotFoundException;
import com.kamko.bankdemo.exception.IdMatchingException;
import com.kamko.bankdemo.exception.NotEnoughFundsException;
import com.kamko.bankdemo.exception.WrongPinException;
import com.kamko.bankdemo.service.AccountService;
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
    private AccountService accountService;

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
    void findOne_success() throws Exception {
        var account = new CreatedAccountDto(1L, "first", BigDecimal.TEN);
        doReturn(account).when(accountService).findOne(anyLong());
        mockMvc.perform(get(BASE_PATH + "/1").accept(APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("first"),
                        jsonPath("$.balance").value("10")
                );
    }

    @Test
    void findOne_accountNotFoundException() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).findOne(anyLong());
        mockMvc.perform(get(BASE_PATH + "/1").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll_success() throws Exception {
        var content = List.of(
                new NameBalanceAccountDto("first", BigDecimal.valueOf(1000)),
                new NameBalanceAccountDto("second", BigDecimal.valueOf(500))
        );
        var pageable = PageRequest.of(0, 2);
        var page = new PageImpl<>(content, pageable, content.size());
        doReturn(page).when(accountService).findAll(anyInt(), anyInt());
        mockMvc.perform(get(BASE_PATH)
                        .accept(APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "1")
                )
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
        var response = new CreatedAccountDto(1L, "first", BigDecimal.ZERO);
        doReturn(response).when(accountService).create(any(CreateAccountDto.class));
        var request = new JSONObject(new HashMap<>(Map.of(
                "name", "first",
                "pin", "1111"
        )));
        mockMvc.perform(post(BASE_PATH)
                        .content(request.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.name").value("first"),
                        jsonPath("$.balance").value(0)
                );
    }

    @Test
    void deposit_success() throws Exception {
        var response = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(90));
        doReturn(response).when(accountService).deposit(any(DepositRequest.class));
        mockMvc.perform(put(BASE_PATH + "/deposit")
                        .content(DEPOSIT_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
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
        var response = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(90));
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
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_wrongPinException() throws Exception {
        doThrow(WrongPinException.class).when(accountService).withdraw(any(WithdrawRequest.class));
        mockMvc.perform(put(BASE_PATH + "/withdraw")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    void transfer_success() throws Exception {
        var response = new CreatedAccountDto(1L, "first", BigDecimal.valueOf(90));
        doReturn(response).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
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
    void transfer_accountNotFoundException() throws Exception {
        doThrow(AccountNotFoundException.class).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void transfer_notEnoughFundsException() throws Exception {
        doThrow(NotEnoughFundsException.class).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(WITHDRAW_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void transfer_wrongPinException() throws Exception {
        doThrow(WrongPinException.class).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    void transfer_idMatchingException() throws Exception {
        doThrow(IdMatchingException.class).when(accountService).transfer(any(TransferRequest.class));
        mockMvc.perform(put(BASE_PATH + "/transfer")
                        .content(TRANSFER_REQUEST.toString())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpectAll(status().isConflict());
    }

}