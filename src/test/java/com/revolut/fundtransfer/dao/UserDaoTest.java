package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.helper.DbConnectionFactory;
import com.revolut.fundtransfer.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;
import static org.junit.Assert.assertTrue;

public class UserDaoTest extends DaoTest {

    @Test
    public void testGetAllUsers() throws FundTransferException {
        List<User> allUsers = DF.getUserDao().getAllUsers();
        assertTrue("getAllUsers() failed", allUsers.size() > 0);
    }

    @Test
    public void testGetUserById() throws FundTransferException {
        User usr = DF.getUserDao().getUserByUserId(2L);
        assertTrue("getUserByUserId() failed", usr.getName().equals("MERRY"));
        usr = DF.getUserDao().getUserByUserId(500L);
        assertTrue("getUserByUserId() failed", usr == null);
    }

    @Test
    public void testGetUserByName() throws FundTransferException {
        User usr = DF.getUserDao().getUserByName("MERRY");
        assertTrue("getUserByName() failed", usr != null);
        usr = DF.getUserDao().getUserByName("JOHN123");
        assertTrue("getUserByName() failed", usr == null);
    }

    @Test
    public void testCreateUser() throws FundTransferException {
        User usr = new User("test123", "test123e@gmail.com");
        long id = DF.getUserDao().createUser(usr);
        User createdUsr = DF.getUserDao().getUserByUserId(id);
        assertTrue("createUser() failed", createdUsr.getName().equals("test123"));
        assertTrue("createUser() failed", usr.getEmailId().equals("test123e@gmail.com"));
    }

    @Test
    public void testUpdateUser() throws FundTransferException {
        User usr = DF.getUserDao().getUserByUserId(1L);
        usr.setEmailId("updatedmail@gmail.com");
        int rowCount = DF.getUserDao().updateUser(1L, usr);
        assertTrue("updateUser() failed", rowCount == 1);
        assertTrue("updateUser() failed", DF.getUserDao().getUserByUserId(1L).getEmailId().equals("updatedmail@gmail.com"));
        usr.setUserId(100L);
        rowCount = DF.getUserDao().updateUser(100L, usr);
        assertTrue("updateUser() failed", rowCount == 0);
    }

    @Test
    public void testDeleteUser() throws FundTransferException {
        int rowCount = DF.getUserDao().deleteUser(1L);
        assertTrue("deleteUser() failed", rowCount == 1);
        assertTrue("deleteUser() failed", DF.getUserDao().getUserByUserId(1L) == null);
        rowCount = DF.getUserDao().deleteUser(1L);
        assertTrue("deleteUser() failed", rowCount == 0);
    }
}
