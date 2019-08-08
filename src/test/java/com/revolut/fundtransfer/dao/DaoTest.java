package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

public class DaoTest {

    private static final Logger LOGGER = LogManager.getLogger(DaoTest.class);

    @BeforeClass
    public static void init() throws FundTransferException {
        LOGGER.info("creating schema with test data");
        DbConnectionFactory.getDbConnectionFactory(DbConnectionFactory.FACTORY_CODE_H2).createSchemaWithTestData();
    }
}
