package com.revolut.fundtransfer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class UserAccount {

    @JsonProperty(required = false)
    private long accountId;

    @JsonProperty(required = true)
    private String userName;

    @JsonProperty(required = true)
    private BigDecimal balanceAmount;

    @JsonProperty(required = true)
    private String currencyCode;

    public UserAccount() {
    }

    public UserAccount(String userName, BigDecimal balanceAmount, String currencyCode) {
        this.userName = userName;
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }

    public UserAccount(long accountId, String userName, BigDecimal balanceAmount, String currencyCode) {
        this.accountId = accountId;
        this.userName = userName;
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return accountId == that.accountId &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(balanceAmount, that.balanceAmount) &&
                Objects.equals(currencyCode, that.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userName, balanceAmount, currencyCode);
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "accountId=" + accountId +
                ", userName='" + userName + '\'' +
                ", balanceAmount=" + balanceAmount +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
