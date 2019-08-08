package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.User;

import java.util.List;

public interface UserDao {

    List<User> getAllUsers() throws FundTransferException;

    User getUserByUserId(long userId) throws FundTransferException;

    User getUserByName(String userName) throws FundTransferException;

    long createUser(User user) throws FundTransferException;

    int updateUser(Long userId, User user) throws FundTransferException;

    int deleteUser(long userId) throws FundTransferException;

}
