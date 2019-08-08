package com.revolut.fundtransfer.helper;

import com.revolut.fundtransfer.exception.FundTransferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public abstract class DbConnectionFactory {

    private static final Logger LOGGER = LogManager.getLogger(DbConnectionFactory.class);

    private static DbConnectionFactory _h2DbConnectionFactory = new H2DbConnectionFactory();
    public static final int FACTORY_CODE_H2 = 10;

    public abstract Connection getDbConnection() throws FundTransferException;

    public void closeConnectionQuietly(Connection con) {
        try {
            if (Objects.nonNull(con)) {
                con.close();
            }
        } catch (Exception ex) {
            LOGGER.error("Error while closing Connection quietly : ", ex);
        }
    }

    public void closeStatementQuietly(Statement st) {
        try {
            if (Objects.nonNull(st)) {
                st.close();
            }
        } catch (Exception ex) {
            LOGGER.error("Error while closing Statement quietly : ", ex);
        }
    }


    public void closeResultSetQuietly(ResultSet rs) {
        try {
            if (Objects.nonNull(rs)) {
                rs.close();
            }
        } catch (Exception ex) {
            LOGGER.error("Error while closing ResultSet quietly : ", ex);
        }
    }

    public abstract void createSchemaWithTestData() throws FundTransferException;

    public static DbConnectionFactory getDbConnectionFactory(int factoryCode) {
        if (factoryCode == FACTORY_CODE_H2) {
            return _h2DbConnectionFactory;
        }
        return _h2DbConnectionFactory;
    }

}
