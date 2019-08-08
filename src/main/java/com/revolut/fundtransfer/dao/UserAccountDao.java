package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.FundTransferTransaction;
import com.revolut.fundtransfer.model.UserAccount;

import java.math.BigDecimal;
import java.util.List;

public interface UserAccountDao {
    List<UserAccount> getAllAccounts() throws FundTransferException;
    UserAccount getAccountById(long accountId) throws FundTransferException;
    long createAccount(UserAccount account) throws FundTransferException;
    int deleteAccountById(long accountId) throws FundTransferException;
    int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws FundTransferException;
    int transferFund(FundTransferTransaction moneyTransferTransaction) throws FundTransferException;
}
