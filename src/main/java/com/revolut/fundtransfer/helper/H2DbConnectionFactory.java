package com.revolut.fundtransfer.helper;

import com.revolut.fundtransfer.exception.FundTransferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

import static com.revolut.fundtransfer.helper.AppConfigHelper.ACH;

public class H2DbConnectionFactory extends DbConnectionFactory {

    private static final Logger LOGGER = LogManager.getLogger(H2DbConnectionFactory.class);

    public Connection getDbConnection() throws FundTransferException {
        Connection dbConnection = null;
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("creating db-connection");
            }
            Class.forName(ACH.getPropValue("db.driver"));
            dbConnection = DriverManager.getConnection(ACH.getPropValue("db.connection.url"), ACH.getPropValue("db.user"), ACH.getPropValue("db.password"));
        } catch (Exception e) {
            throw new FundTransferException("error while creating db-connection : " + e.getMessage());
        }
        return dbConnection;
    }


    @Override
    public void createSchemaWithTestData() throws FundTransferException {
        LOGGER.info("creating schema with some test data");
        Connection dbConnection = null;
        try {
            dbConnection = getDbConnection();
            final InputStream fis = H2DbConnectionFactory.class.getClassLoader().getResourceAsStream("schema.sql");
            Reader reader = new InputStreamReader(fis);
            if (Objects.nonNull(reader)) {
                RunScript.execute(dbConnection, reader);
                LOGGER.info("schema created successfully");
            } else {
                throw new FileNotFoundException("schema.sql not found in classpath");
            }
        } catch (Exception e) {
            LOGGER.error("Error while populating test data : ", e);
            throw new FundTransferException("Error while populating test data", e);
        } finally {
            closeConnectionQuietly(dbConnection);
        }
    }
}
