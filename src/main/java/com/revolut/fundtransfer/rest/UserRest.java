package com.revolut.fundtransfer.rest;

import com.revolut.fundtransfer.exception.FundTransferException;
import com.revolut.fundtransfer.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.revolut.fundtransfer.helper.DaoFactory.DF;


@Path("/usr")
@Produces(MediaType.APPLICATION_JSON)
public class UserRest {

    private static final Logger LOGGER = LogManager.getLogger(UserRest.class);

    @GET
    @Path("/all")
    public List<User> getAllUsers() throws FundTransferException {
        return DF.getUserDao().getAllUsers();
    }

    @GET
    @Path("/{name}")
    public User getUserByName(@PathParam("name") String name) throws FundTransferException {
        LOGGER.info("getting user by name : " + name);

        final User user = DF.getUserDao().getUserByName(name);
        if (user == null) {
            throw new WebApplicationException("User not found with name : " + name, Response.Status.NOT_FOUND);
        }
        return user;
    }


    @POST
    @Path("/create")
    public User createUser(User user) throws FundTransferException {
        LOGGER.info("creating user : " + user.getName());
        if (DF.getUserDao().getUserByName(user.getName()) != null) {
            throw new WebApplicationException("User with name " + user.getName() + " already exists", Response.Status.BAD_REQUEST);
        }
        final long uId = DF.getUserDao().createUser(user);
        return DF.getUserDao().getUserByUserId(uId);
    }

    @PUT
    @Path("/{userId}")
    public Response updateUser(@PathParam("userId") long userId, User user) throws FundTransferException {
        LOGGER.info("creating user with ID : " + user.getUserId());
        final int updateCount = DF.getUserDao().updateUser(userId, user);
        if (updateCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteUser(@PathParam("userId") long userId) throws FundTransferException {
        LOGGER.info("deleting user with ID : " + userId);
        int deleteCount = DF.getUserDao().deleteUser(userId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
