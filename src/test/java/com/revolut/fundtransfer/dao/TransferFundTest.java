package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import com.revolut.fundtransfer.model.FundTransferTransaction;
import com.revolut.fundtransfer.model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;
import static com.revolut.fundtransfer.helper.DbConnectionFactory.FACTORY_CODE_H2;
import static com.revolut.fundtransfer.helper.DbConnectionFactory.getDbConnectionFactory;
import static junit.framework.TestCase.assertTrue;

public class TransferFundTest extends DaoTest {

    private static final Logger LOGGER = LogManager.getLogger(TransferFundTest.class);
    private final static DbConnectionFactory connectionFactory = getDbConnectionFactory(FACTORY_CODE_H2);

    @After
    public void tearDown() {

    }

    @Test
    public void testSingleThreadFundTransfer() throws FundTransferException {
        final UserAccountDao accountDAO = DF.getUserAccountDao();
        BigDecimal transferAmount = new BigDecimal(800.98746).setScale(4, RoundingMode.HALF_EVEN);
        FundTransferTransaction transaction = new FundTransferTransaction("EUR", transferAmount, 3L, 4L);
        long startTime = System.currentTimeMillis();
        accountDAO.transferFund(transaction);
        long endTime = System.currentTimeMillis();
        LOGGER.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");
        UserAccount accountFrom = accountDAO.getAccountById(3);
        UserAccount accountTo = accountDAO.getAccountById(4);
        LOGGER.debug("Account From: " + accountFrom);
        LOGGER.debug("Account From: " + accountTo);
        assertTrue("transferFund() failed", accountFrom.getBalanceAmount().compareTo(new BigDecimal(2199.0125).setScale(4, RoundingMode.HALF_EVEN)) == 0);
        assertTrue("transferFund() failed", accountTo.getBalanceAmount().equals(new BigDecimal(4800.9875).setScale(4, RoundingMode.HALF_EVEN)));
    }

    @Test
    public void testMultiThreadFundTransfer() throws Exception {
        final UserAccountDao accountDAO = DF.getUserAccountDao();
        // transfer a total of 2000USD from 1000USD balance in multi-threaded mode, expect half of the transaction fail
        int threadCount = 100;
        final CountDownLatch _cdLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    FundTransferTransaction transaction = new FundTransferTransaction("USD",
                            new BigDecimal(20).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
                    accountDAO.transferFund(transaction);
                } catch (Exception e) {
                    LOGGER.error("error occurred while transferring fund : ", e);
                } finally {
                    _cdLatch.countDown();
                }

            }).start();
        }
        _cdLatch.await();
        UserAccount accountFrom = accountDAO.getAccountById(1L);
        UserAccount accountTo = accountDAO.getAccountById(2L);
        LOGGER.debug("Account From: " + accountFrom);
        LOGGER.debug("Account From: " + accountTo);
        assertTrue("transferFund() failed", accountFrom.getBalanceAmount().equals(new BigDecimal(0.0000).setScale(4, RoundingMode.HALF_EVEN)));
        assertTrue("transferFund() failed", accountTo.getBalanceAmount().equals(new BigDecimal(3000.0000).setScale(4, RoundingMode.HALF_EVEN)));
    }

    @Test
    public void testTransferFailOnDBLock() throws Exception {
        final String SQL_LOCK_ACC = "SELECT * FROM user_account WHERE ACCOUNT_ID = 5 FOR UPDATE";
        Connection conn = null;
        PreparedStatement lockStmt = null;
        ResultSet rs = null;
        UserAccount fromAccount = null;
        try {
            conn = connectionFactory.getDbConnection();
            conn.setAutoCommit(false);
            // lock account for writing:
            lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new UserAccount(rs.getLong("ACCOUNT_ID"), rs.getString("USER_NAME"),
                        rs.getBigDecimal("BALANCE_AMOUNT"), rs.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Locked Account: " + fromAccount);
            }

            if (fromAccount == null) {
                throw new FundTransferException("Locking error during test, SQL = " + SQL_LOCK_ACC);
            }
            // after lock account 5, try to transfer from account 6 to 5 , default h2 timeout for acquire lock is 1sec
            BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);
            FundTransferTransaction transaction = new FundTransferTransaction("GBP", transferAmount, 6L, 5L);
            DF.getUserAccountDao().transferFund(transaction);
            conn.commit();
        } catch (Exception e) {
            LOGGER.error("Exception occurred, initiate a rollback");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException re) {
                LOGGER.error("Fail to rollback transaction", re);
            }
        } finally {
            connectionFactory.closeResultSetQuietly(rs);
            connectionFactory.closeStatementQuietly(lockStmt);
            connectionFactory.closeConnectionQuietly(conn);
        }
        // now inspect account 3 and 4 to verify no transaction occurred
        BigDecimal originalBalance = new BigDecimal(6000.0000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("transferFund() failed", DF.getUserAccountDao().getAccountById(6).getBalanceAmount().equals(originalBalance));
        originalBalance = new BigDecimal(5000.0000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("transferFund() failed", DF.getUserAccountDao().getAccountById(5).getBalanceAmount().equals(originalBalance));
    }
}
