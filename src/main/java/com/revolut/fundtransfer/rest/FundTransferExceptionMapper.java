package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.exception.FundTransferErrorResponse;
import com.revolut.fundtransfer.exception.FundTransferException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// JAX-RS providers are application components that allow for customization of runtime behavior in three key areas:
// data binding, exception mapping, and context resolution (for example, providing JAXBContext instances to the runtime).
// Each JAX-RS provider class must be annotated with the @Provider annotation
// The JAX-RS specification allows you to plug in your own request/response body reader and writers
@Provider
public class FundTransferExceptionMapper implements ExceptionMapper<FundTransferException> {

    private static final Logger LOGGER = LogManager.getLogger(UserRest.class);

    public Response toResponse(FundTransferException daoException) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("mapping MoneyTransferException to Response");
        }
        FundTransferErrorResponse errorResponse = new FundTransferErrorResponse();
        errorResponse.setErrorCode(daoException.getMessage());

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
    }

}
