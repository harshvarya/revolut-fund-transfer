package com.revolut.fundtransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class FundTransferTransaction {

    @JsonProperty(required = true)
    private String currencyCode;

    @JsonProperty(required = true)
    private BigDecimal amount;

    @JsonProperty(required = true)
    private Long fromAccountId;

    @JsonProperty(required = true)
    private Long toAccountId;

    public FundTransferTransaction() {
    }

    public FundTransferTransaction(String currencyCode, BigDecimal amount, Long fromAccountId, Long toAccountId) {
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FundTransferTransaction that = (FundTransferTransaction) o;
        return Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(fromAccountId, that.fromAccountId) &&
                Objects.equals(toAccountId, that.toAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode, amount, fromAccountId, toAccountId);
    }

    @Override
    public String toString() {
        return "MoneyTransfer{" +
                "currencyCode='" + currencyCode + '\'' +
                ", amount=" + amount +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                '}';
    }
}
