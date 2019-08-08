package com.revolut.fundtransfer.helper;

import static org.junit.Assert.*;
import org.junit.Test;

public class CurrencyHelperTest {

    @Test
    public void testValidateCurrencyCode(){
        assertTrue("validateCurrencyCode() failed", CurrencyHelper.CH.validateCurrencyCode("EUR"));
        assertFalse("validateCurrencyCode() failed", CurrencyHelper.CH.validateCurrencyCode("EUR1"));
    }
}
