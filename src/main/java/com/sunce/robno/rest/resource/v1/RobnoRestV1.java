package com.sunce.robno.rest.resource.v1;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sunce.robno.rest.RobnoRestApp;
import com.sunce.robno.rest.dto.UserCredentials;
import com.sunce.robno.rest.manager.UserAuthenticator;

@Path("v1/auth")
@Produces(MediaType.APPLICATION_JSON)
public class RobnoRestV1 {

    UserAuthenticator userAuthenticator;

    @Inject
    public RobnoRestV1(UserAuthenticator userAuthenticator) {
        this.userAuthenticator = userAuthenticator;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public UUID logUser(@QueryParam("username") String username, @QueryParam("password") String password) { //UserCredentials creds
    	UserCredentials creds = new UserCredentials();
    	creds.setUsername(username);
    	creds.setPassword(password);
    	
    	int uid =  userAuthenticator.authenticateUser(creds);
    	UUID uuid = RobnoRestApp.logUser(uid);
    	
    	if (uuid == null) {
    		throw new NotAuthorizedException("Bad credentials");
    	}
    	
    	return uuid;
    }
}
