package com.revolut.fundtransfer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FundTransferSuccessMessage {
    @JsonProperty(required = false)
    String message;

    public FundTransferSuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
