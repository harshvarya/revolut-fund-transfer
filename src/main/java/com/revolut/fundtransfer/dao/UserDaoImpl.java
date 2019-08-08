package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import com.revolut.fundtransfer.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.revolut.fundtransfer.helper.DbConnectionFactory.FACTORY_CODE_H2;
import static com.revolut.fundtransfer.helper.DbConnectionFactory.getDbConnectionFactory;

public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);

    private final static String SQL_GET_ALL_USERS = "SELECT * FROM user";
    private final static String SQL_GET_USER_BY_USER_ID = "SELECT * FROM user WHERE USER_ID = ?";
    private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM user WHERE NAME = ?";
    private final static String SQL_CREATE_USER = "INSERT INTO user (NAME, EMAIL_ID) VALUES (?, ?)";
    private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM user WHERE USER_ID = ?";
    private final static String SQL_UPDATE_USER = "UPDATE user SET NAME = ?, EMAIL_ID = ? WHERE USER_ID = ?";

    private final static DbConnectionFactory connectionFactory = getDbConnectionFactory(FACTORY_CODE_H2);

    @Override
    public List<User> getAllUsers() throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        List<User> users = new ArrayList<>();

        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_GET_ALL_USERS);
            resultSet = prepStmt.executeQuery();

            while (resultSet.next()) {
                User usr = new User(resultSet.getLong("USER_ID"), resultSet.getString("NAME"), resultSet.getString("EMAIL_ID"));
                users.add(usr);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getAllUsers() : " + usr);
                }
            }
            return users;
        } catch (Exception e) {
            throw new FundTransferException("Error while getting all users : ", e);
        } finally {
            connectionFactory.closeResultSetQuietly(resultSet);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    @Override
    public User getUserByUserId(long userId) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        User usr = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_GET_USER_BY_USER_ID);
            prepStmt.setLong(1, userId);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                usr = new User(resultSet.getLong("USER_ID"), resultSet.getString("NAME"), resultSet.getString("EMAIL_ID"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("getUserByUserId() : " + usr);
            }
            return usr;
        } catch (SQLException e) {
            throw new FundTransferException("Error while getting user by userId : ", e);
        } finally {
            connectionFactory.closeResultSetQuietly(resultSet);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public User getUserByName(String name) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        User u = null;
        User usr = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_GET_USER_BY_NAME);
            prepStmt.setString(1, name);
            resultSet = prepStmt.executeQuery();
            if (resultSet.next()) {
                usr = new User(resultSet.getLong("USER_ID"), resultSet.getString("NAME"), resultSet.getString("EMAIL_ID"));
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("getUserByName() : " + usr);
            }
            return usr;
        } catch (SQLException e) {
            throw new FundTransferException("Error reading user data", e);
        } finally {
            connectionFactory.closeResultSetQuietly(resultSet);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public long createUser(User user) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        ResultSet generatedKeys = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, user.getName());
            prepStmt.setString(2, user.getEmailId());
            int affectedRows = prepStmt.executeUpdate();
            if (affectedRows == 0) {
                LOGGER.error("createUser(): creating a new user failed" + user);
                throw new FundTransferException("User can't be created");
            }
            generatedKeys = prepStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                LOGGER.error("createUser(): creating a new user failed" + user);
                throw new FundTransferException("User can't be created");
            }
        } catch (SQLException e) {
            LOGGER.error("Error creating User : " + user);
            throw new FundTransferException("Error creating user data", e);
        } finally {
            connectionFactory.closeResultSetQuietly(generatedKeys);
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }

    public int updateUser(Long userId, User user) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_UPDATE_USER);
            prepStmt.setString(1, user.getName());
            prepStmt.setString(2, user.getEmailId());
            prepStmt.setLong(3, userId);
            return prepStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error Updating User :" + user);
            throw new FundTransferException("Error update user data", e);
        } finally {
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }


    public int deleteUser(long userId) throws FundTransferException {
        Connection dbConnection = null;
        PreparedStatement prepStmt = null;
        try {
            dbConnection = connectionFactory.getDbConnection();
            prepStmt = dbConnection.prepareStatement(SQL_DELETE_USER_BY_ID);
            prepStmt.setLong(1, userId);
            return prepStmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error Deleting User :" + userId);
            throw new FundTransferException("Error Deleting User ID:" + userId, e);
        } finally {
            connectionFactory.closeStatementQuietly(prepStmt);
            connectionFactory.closeConnectionQuietly(dbConnection);
        }
    }
}
