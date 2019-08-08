package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.UserAccount;
import com.revolut.fundtransfer.helper.CurrencyHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;


@Path("/ac")
@Produces(MediaType.APPLICATION_JSON)
public class UserAccountRest {

    private static final Logger LOGGER = LogManager.getLogger(UserAccountRest.class);

    @GET
    @Path("/all")
    public List<UserAccount> getAllAccounts() throws FundTransferException {
        LOGGER.info("getting all accounts");
        return DF.getUserAccountDao().getAllAccounts();
    }

    @GET
    @Path("/{accountId}")
    public UserAccount getAccount(@PathParam("accountId") long accountId) throws FundTransferException {
        LOGGER.info("getting Account by ID : "+accountId);
        return DF.getUserAccountDao().getAccountById(accountId);
    }

    @GET
    @Path("/{accountId}/balance")
    public BigDecimal getBalance(@PathParam("accountId") long accountId) throws FundTransferException {
        LOGGER.info("getting Account balance by: "+accountId);
        final UserAccount account = DF.getUserAccountDao().getAccountById(accountId);

        if (account == null) {
            throw new WebApplicationException("Account not found", Response.Status.NOT_FOUND);
        }
        return account.getBalanceAmount();
    }

    @POST
    @Path("/create")
    public UserAccount createAccount(UserAccount account) throws FundTransferException {
        LOGGER.info("creating a new Account with user name : "+account.getUserName());
        final long accountId = DF.getUserAccountDao().createAccount(account);
        return DF.getUserAccountDao().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/deposit/{amount}")
    public UserAccount deposit(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws FundTransferException {
        LOGGER.info("depositing amount"+ amount+" to account ID : "+accountId);
        if (amount.compareTo(CurrencyHelper.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }

        DF.getUserAccountDao().updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
        return DF.getUserAccountDao().getAccountById(accountId);
    }

    @PUT
    @Path("/{accountId}/withdraw/{amount}")
    public UserAccount withdraw(@PathParam("accountId") long accountId, @PathParam("amount") BigDecimal amount) throws FundTransferException {

        if (amount.compareTo(CurrencyHelper.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Withdraw amount", Response.Status.BAD_REQUEST);
        }
        LOGGER.info("withdrawing amount  " + amount + "from account ID = " + accountId);
        DF.getUserAccountDao().updateAccountBalance(accountId, amount.negate().setScale(4, RoundingMode.HALF_EVEN));
        return DF.getUserAccountDao().getAccountById(accountId);
    }

    @DELETE
    @Path("/{accountId}")
    public Response deleteAccount(@PathParam("accountId") long accountId) throws FundTransferException {
        LOGGER.info("deleting Account by ID : "+accountId);
        int deleteCount = DF.getUserAccountDao().deleteAccountById(accountId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
