package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.FundTransferSuccessMessage;
import com.revolut.fundtransfer.model.FundTransferTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;
import static com.revolut.fundtransfer.helper.CurrencyHelper.CH;

@Path("/trans")
@Produces(MediaType.APPLICATION_JSON)
public class FundTransferRest {
    private static final Logger LOGGER = LogManager.getLogger(FundTransferRest.class);

    @POST
    public Response transferFund(FundTransferTransaction transaction) throws FundTransferException {
        LOGGER.info("transferring fund with transaction : " + transaction);
        String currency = transaction.getCurrencyCode();
        if (CH.validateCurrencyCode(currency)) {
            int updateCount = DF.getUserAccountDao().transferFund(transaction);
            if (updateCount == 2) {
                return Response.ok(new FundTransferSuccessMessage("FUND TRANSFERRED SUCCESSFULLY")).build();
            } else {
                throw new WebApplicationException("Transaction failed", Response.Status.BAD_REQUEST);
            }
        } else {
            throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
        }
    }

}
