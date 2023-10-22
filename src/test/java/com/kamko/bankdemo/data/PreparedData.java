package com.kamko.bankdemo.data;

import com.kamko.bankdemo.dto.account.AccountIdNameBalanceDto;
import com.kamko.bankdemo.dto.account.AccountNameBalanceDto;
import com.kamko.bankdemo.dto.account_operation.DepositRequest;
import com.kamko.bankdemo.dto.account_operation.WithdrawRequest;
import com.kamko.bankdemo.entity.Account;
import com.kamko.bankdemo.entity.Operation;
import com.kamko.bankdemo.entity.TransactionLog;

import java.math.BigDecimal;
import java.util.List;

public class PreparedData {

    public static final Account ACCOUNT = prepareAccount();
    public static final AccountIdNameBalanceDto ACCOUNT_RESPONSE = prepareAccountResponse();
    public static final AccountNameBalanceDto NAME_BALANCE_ACCOUNT_RESPONSE = prepareNamedBalanceAccountResponse();
    public static final TransactionLog DEPOSIT_TRANSACTION_LOG = prepareDepositLog();
    public static final TransactionLog WITHDRAW_TRANSACTION_LOG = prepareWithdrawLog();
    public static final DepositRequest DEPOSIT_REQUEST = prepareDepositRequest();
    public static final WithdrawRequest WITHDRAW_REQUEST = prepareWithdrawRequest();
    public static final WithdrawRequest WRONG_AMOUNT_WITHDRAW_REQUEST = prepareWrongAmountWithdrawRequest();
    public static final List<Account> PAGE_CONTENT = preparePageContent();

    private static Account prepareAccount() {
        var account = new Account();
        account.setId(1L);
        account.setName("first");
        account.setBalance(BigDecimal.valueOf(200));
        account.setPin("1111");
        return account;
    }

    private static AccountIdNameBalanceDto prepareAccountResponse() {
        return new AccountIdNameBalanceDto(1L, "first", BigDecimal.valueOf(200));
    }

    private static TransactionLog prepareDepositLog() {
        return new TransactionLog(Operation.DEPOSIT, BigDecimal.TEN, ACCOUNT);
    }

    private static TransactionLog prepareWithdrawLog() {
        return new TransactionLog(Operation.WITHDRAW, BigDecimal.TEN, ACCOUNT);
    }

    private static DepositRequest prepareDepositRequest() {
        return new DepositRequest(ACCOUNT.getId(), BigDecimal.TEN);
    }

    private static WithdrawRequest prepareWithdrawRequest() {
        return new WithdrawRequest(ACCOUNT.getId(), BigDecimal.TEN, ACCOUNT.getPin());
    }

    private static WithdrawRequest prepareWrongAmountWithdrawRequest() {
        return new WithdrawRequest(ACCOUNT.getId(), BigDecimal.valueOf(1_000), ACCOUNT.getPin());
    }

    private static List<Account> preparePageContent() {
        return List.of(ACCOUNT);
    }

    private static AccountNameBalanceDto prepareNamedBalanceAccountResponse() {
        return new AccountNameBalanceDto(ACCOUNT.getName(), ACCOUNT.getBalance());
    }

}
