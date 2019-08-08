package com.revolut.fundtransfer.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public enum CurrencyHelper {

    CH;

    static final Logger LOGGER = LogManager.getLogger(CurrencyHelper.class);

    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

    public boolean validateCurrencyCode(String ccyCode) {
        try {
            Currency instance = Currency.getInstance(ccyCode);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Validating Currency Code: " + instance.getSymbol());
            }
            return instance.getCurrencyCode().equals(ccyCode);
        } catch (Exception e) {
            LOGGER.error("Can't parse the entered Currency Code, Validation Failed: ", e);
        }
        return false;
    }

}
