package com.revolut.fundtransfer.exception;

public class FundTransferException extends Exception {
    private static final long serialVersionUID = 98463217896L;

    public FundTransferException(String msg) {
        super(msg);
    }

    public FundTransferException(String msg, Throwable cause) {
        super(msg, cause);
    }
}