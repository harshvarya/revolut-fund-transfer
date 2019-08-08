package com.revolut.fundtransfer.helper;

import com.revolut.fundtransfer.dao.UserAccountDao;
import com.revolut.fundtransfer.dao.UserAccountDaoImpl;
import com.revolut.fundtransfer.dao.UserDao;
import com.revolut.fundtransfer.dao.UserDaoImpl;

public enum DaoFactory {
    DF;
    public static UserDao getUserDao() {
        return new UserDaoImpl();
    }
    public static UserAccountDao getUserAccountDao() {
        return new UserAccountDaoImpl();
    }
}
