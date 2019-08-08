package com.revolut.fundtransfer.helper;

import static org.junit.Assert.*;
import org.junit.Test;

public class AppConfigHelperTest {
    @Test
    public void testGetPropValue(){
        assertEquals("getPropValue() failed", "jdbc:h2:mem:fundtransferdb;DB_CLOSE_DELAY=-1", AppConfigHelper.ACH.getPropValue("db.connection.url"));
        assertEquals("getPropValue() failed", "org.h2.Driver", AppConfigHelper.ACH.getPropValue("db.driver"));
        assertEquals("getPropValue() failed", "sa", AppConfigHelper.ACH.getPropValue("db.user"));
        assertEquals("getPropValue() failed", "sa", AppConfigHelper.ACH.getPropValue("db.password"));
    }
}
