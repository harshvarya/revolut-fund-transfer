package com.revolut.fundtransfer.dao;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.UserAccount;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;
import static junit.framework.TestCase.assertTrue;

public class UserAccountDaoTest extends DaoTest {

    @Test
    public void testGetAllAccounts() throws FundTransferException {
        List<UserAccount> allAccounts = DF.getUserAccountDao().getAllAccounts();
        assertTrue("getAllAccounts() failed", allAccounts.size() > 0);
    }

    @Test
    public void testGetAccountById() throws FundTransferException {
        UserAccount account = DF.getUserAccountDao().getAccountById(1L);
        assertTrue("getAllAccounts() failed", account.getUserName().equals("JOHN"));
        account = DF.getUserAccountDao().getAccountById(100L);
        assertTrue("getAllAccounts() failed", account == null);
    }


    @Test
    public void testCreateAccount() throws FundTransferException {
        BigDecimal balanceAmount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
        UserAccount usrAct = new UserAccount("TEST896", balanceAmount, "EUR");
        long aid = DF.getUserAccountDao().createAccount(usrAct);
        UserAccount createdAc = DF.getUserAccountDao().getAccountById(aid);
        assertTrue("createAccount() failed", createdAc.getBalanceAmount().equals(balanceAmount));
        assertTrue("createAccount() failed", createdAc.getUserName().equals("TEST896"));
        assertTrue("createAccount() failed", createdAc.getCurrencyCode().equals("EUR"));
    }

    @Test
    public void testDeleteAccountById() throws FundTransferException {
        int rowsCount = DF.getUserAccountDao().deleteAccountById(2L);
        assertTrue("deleteAccountById() failed", rowsCount == 1);
        assertTrue("deleteAccountById() failed", DF.getUserAccountDao().getAccountById(2L) == null);
        rowsCount = DF.getUserAccountDao().deleteAccountById(1000L);
        assertTrue("deleteAccountById() failed", rowsCount == 0);
    }

    @Test(expected = FundTransferException.class)
    public void testUpdateAccountBalance() throws FundTransferException {
        long accountId = 2L;
        BigDecimal depositAmount = new BigDecimal(5000).setScale(4, RoundingMode.HALF_EVEN);
        int rowsCount = DF.getUserAccountDao().updateAccountBalance(accountId, depositAmount);
        BigDecimal updatedAmount = new BigDecimal(7000).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("updateAccountBalance() failed", rowsCount == 1);
        assertTrue("updateAccountBalance() failed", DF.getUserAccountDao().getAccountById(accountId).getBalanceAmount().equals(updatedAmount));
        BigDecimal withdrawAmount = new BigDecimal(-200).setScale(4, RoundingMode.HALF_EVEN);
        rowsCount = DF.getUserAccountDao().updateAccountBalance(accountId, withdrawAmount);
        updatedAmount = new BigDecimal(6800).setScale(4, RoundingMode.HALF_EVEN);
        assertTrue("updateAccountBalance() failed", rowsCount == 1);
        assertTrue("updateAccountBalance() failed", DF.getUserAccountDao().getAccountById(accountId).getBalanceAmount().equals(updatedAmount));
        withdrawAmount = new BigDecimal(-20000).setScale(4, RoundingMode.HALF_EVEN);
        rowsCount = DF.getUserAccountDao().updateAccountBalance(1L, withdrawAmount);
        assertTrue("updateAccountBalance() failed", rowsCount == 0);
    }
}