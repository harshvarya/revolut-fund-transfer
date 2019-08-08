package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import com.revolut.fundtransfer.helper.CurrencyHelper;
import com.revolut.fundtransfer.model.FundTransferTransaction;
import com.revolut.fundtransfer.model.UserAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.fundtransfer.helper.DbConnectionFactory.FACTORY_CODE_H2;
import static com.revolut.fundtransfer.helper.DbConnectionFactory.getDbConnectionFactory;

public class UserAccountDaoImpl implements UserAccountDao {

    private static final Logger LOGGER = LogManager.getLogger(UserAccountDaoImpl.class);

    private final static String SQL_GET_ALL_ACC = "SELECT * FROM user_account";
    private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM user_account WHERE ACCOUNT_ID = ? ";
    private final static String SQL_CREATE_ACC = "INSERT INTO user_account (USER_NAME, CURRENCY_CODE, BALANCE_AMOUNT) VALUES (?, ?, ?)";
    private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM user_account WHERE ACCOUNT_ID = ?";
    private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE user_account SET BALANCE_AMOUNT = ? WHERE ACCOUNT_ID = ? ";
    private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM user_account WHERE ACCOUNT_ID = ? FOR UPDATE";

    private final static DbConnectionFactory connectionFactory = getDbConnectionFactory(FACTORY_CODE_H2);

    public List<UserAccount> getAllAccounts() throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        List<UserAccount> allAccounts = new ArrayList<UserAccount>();
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_GET_ALL_ACC);
            resultSet = prepStmt.executeQuery();
            while (resultSet.next()) {
                UserAccount acc = new UserAccount(resultSet.getLong("ACCOUNT_ID"), resultSet.getString("USER_NAME"),
                        resultSet.getBigDecimal("BALANCE_AMOUNT"), resultSet.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("getAllAccounts() :" + acc);
                allAccounts.add(acc);
            }
            return allAccounts;
        } catch (SQLException e) {
            throw new FundTransferException("getAccountById(): Error reading account data", e);
        } finally {
            connectionFactory.closeResultSetQuietly(resultSet);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public UserAccount getAccountById(long accountId) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        UserAccount acc = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_GET_ACC_BY_ID);
            prepStmt.setLong(1, accountId);
            resultSet = prepStmt.executeQuery();
            if (resultSet.next()) {
                acc = new UserAccount(resultSet.getLong("ACCOUNT_ID"), resultSet.getString("USER_NAME"), resultSet.getBigDecimal("BALANCE_AMOUNT"),
                        resultSet.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("getAccountById() : " + acc);
            }
            return acc;
        } catch (SQLException e) {
            throw new FundTransferException("getAccountById()- Error reading account data : ", e);
        } finally {
            connectionFactory.closeResultSetQuietly(resultSet);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public long createAccount(UserAccount account) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            preparedStatement = dbConnection.prepareStatement(SQL_CREATE_ACC, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUserName());
            preparedStatement.setString(2, account.getCurrencyCode());
            preparedStatement.setBigDecimal(3, account.getBalanceAmount());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.error("createAccount(): Creating account failed, no rows affected.");
                throw new FundTransferException("Account Cannot be created");
            }
            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                LOGGER.error("Creating account failed, no ID obtained.");
                throw new FundTransferException("Account can't be created");
            }
        } catch (SQLException e) {
            LOGGER.error("Error Inserting Account  " + account);
            throw new FundTransferException("createAccount() - Error creating user account : " + account, e);
        } finally {
            connectionFactory.closeResultSetQuietly(generatedKeys);
            connectionFactory.closeStatementQuietly(preparedStatement);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public int deleteAccountById(long accountId) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_DELETE_ACC_BY_ID);
            prepStmt.setLong(1, accountId);
            return prepStmt.executeUpdate();
        } catch (SQLException e) {
            throw new FundTransferException("deleteAccountById() -  Error deleting user account Id : " + accountId, e);
        } finally {
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        UserAccount targetAccount = null;
        int updateCount = -1;
        try {
            dbConnection = connectionFactory.getDbConnection();
            dbConnection.setAutoCommit(false);
            // lock account for writing:
            lockStmt = dbConnection.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, accountId);
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                targetAccount = new UserAccount(rs.getLong("ACCOUNT_ID"), rs.getString("USER_NAME"),
                        rs.getBigDecimal("BALANCE_AMOUNT"), rs.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("updateAccountBalanceAmount() target Account: " + targetAccount);
            }

            if (targetAccount == null) {
                throw new FundTransferException("updateAccountBalanceAmount(): fail to lock account : " + accountId);
            }
            // update account upon success locking
            BigDecimal balance = targetAccount.getBalanceAmount().add(deltaAmount);
            if (balance.compareTo(CurrencyHelper.zeroAmount) < 0) {
                throw new FundTransferException("Not sufficient Fund for account: " + accountId);
            }

            updateStmt = dbConnection.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, balance);
            updateStmt.setLong(2, accountId);
            updateCount = updateStmt.executeUpdate();
            dbConnection.commit();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("New BALANCE_AMOUNT after Update: " + targetAccount);
            return updateCount;
        } catch (SQLException ex) {
            // rollback transaction if exception occurs
            LOGGER.error("updateAccountBalanceAmount(): User Transaction Failed, rollback initiated for: " + accountId, ex);
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (SQLException re) {
                throw new FundTransferException("Fail to rollback transaction", re);
            }
        } finally {
            connectionFactory.closeResultSetQuietly(rs);
            connectionFactory.closeStatementQuietly(lockStmt);
            connectionFactory.closeStatementQuietly(updateStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
        return updateCount;
    }

    public int transferFund(FundTransferTransaction moneyTransferTransaction) throws FundTransferException {
        int result = -1;
        Connection dbConnection = null;
        PreparedStatement lockStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        UserAccount fromAccount = null;
        UserAccount toAccount = null;

        try {
            dbConnection = connectionFactory.getDbConnection();
            dbConnection.setAutoCommit(false);
            // lock the credit and debit account for writing:
            lockStmt = dbConnection.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, moneyTransferTransaction.getFromAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                fromAccount = new UserAccount(rs.getLong("ACCOUNT_ID"), rs.getString("USER_NAME"),
                        rs.getBigDecimal("BALANCE_AMOUNT"), rs.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("transferring fund from Account: " + fromAccount);
            }
            lockStmt = dbConnection.prepareStatement(SQL_LOCK_ACC_BY_ID);
            lockStmt.setLong(1, moneyTransferTransaction.getToAccountId());
            rs = lockStmt.executeQuery();
            if (rs.next()) {
                toAccount = new UserAccount(rs.getLong("ACCOUNT_ID"), rs.getString("USER_NAME"), rs.getBigDecimal("BALANCE_AMOUNT"),
                        rs.getString("CURRENCY_CODE"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("transferring fund to Account: "+ toAccount);
            }

            // check locking status
            if (fromAccount == null || toAccount == null) {
                throw new FundTransferException("Fail to lock both accounts for write");
            }

            // check transaction currency
            if (!fromAccount.getCurrencyCode().equals(moneyTransferTransaction.getCurrencyCode())) {
                throw new FundTransferException(
                        "Fail to transfer Fund, transaction ccy are different from source/destination");
            }

            // check ccy is the same for both accounts
            if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
                throw new FundTransferException(
                        "Fail to transfer Fund, the source and destination account are in different currency");
            }

            // check enough fund in source account
            BigDecimal fromAccountLeftOver = fromAccount.getBalanceAmount().subtract(moneyTransferTransaction.getAmount());
            if (fromAccountLeftOver.compareTo(CurrencyHelper.zeroAmount) < 0) {
                throw new FundTransferException("Not enough Fund from source Account ");
            }
            // proceed with update
            updateStmt = dbConnection.prepareStatement(SQL_UPDATE_ACC_BALANCE);
            updateStmt.setBigDecimal(1, fromAccountLeftOver);
            updateStmt.setLong(2, moneyTransferTransaction.getFromAccountId());
            updateStmt.addBatch();
            updateStmt.setBigDecimal(1, toAccount.getBalanceAmount().add(moneyTransferTransaction.getAmount()));
            updateStmt.setLong(2, moneyTransferTransaction.getToAccountId());
            updateStmt.addBatch();
            int[] rowsUpdated = updateStmt.executeBatch();
            result = rowsUpdated[0] + rowsUpdated[1];
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Number of rows updated for the transfer : " + result);
            }
            // If there is no error, commit the transaction
            dbConnection.commit();
        } catch (SQLException se) {
            // rollback transaction if exception occurs
            LOGGER.error("transferFunf(): User Transaction Failed, rollback initiated for: " + moneyTransferTransaction,
                    se);
            try {
                if (dbConnection != null)
                    dbConnection.rollback();
            } catch (SQLException re) {
                throw new FundTransferException("Fail to rollback transaction", re);
            }
        } finally {
            connectionFactory.closeResultSetQuietly(rs);
            connectionFactory.closeStatementQuietly(lockStmt);
            connectionFactory.closeStatementQuietly(updateStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
        return result;
    }
}
