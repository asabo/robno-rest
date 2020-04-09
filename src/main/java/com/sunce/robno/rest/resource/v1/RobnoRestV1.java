package com.sunce.robno.rest.resource.v1;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ansa.dao.net.Korisnik;
import com.sunce.robno.rest.RobnoRestApp;
import com.sunce.robno.rest.manager.UserAuthenticator;

@Path("v1/auth")
@Produces(MediaType.APPLICATION_JSON)
public class RobnoRestV1 {

    UserAuthenticator userAuthenticator;

    @Inject
    public RobnoRestV1(UserAuthenticator userAuthenticator) {
        this.userAuthenticator = userAuthenticator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Korisnik logUser(Korisnik ulaz) { //UserCredentials creds
    	 
    	Korisnik kor  =  userAuthenticator.authenticateUser(ulaz);
    	if (kor==null) 
    		throw new NotAuthorizedException("Bad credentials");
    	
    	UUID uuid = RobnoRestApp.logUser(kor.getUserId());
    	
    	if (uuid == null) {
    		throw new NotAuthorizedException("Bad credentials");
    	}
    	
    	kor.setUsername(ulaz.getUsername());
    	kor.setUuid(uuid);
    	
    	return kor;
    }
    
    @POST
    @Path("/changePass")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean changePass(Korisnik ulaz) { //UserCredentials creds
    	 
    	Boolean rez  =  userAuthenticator.changePassword(ulaz);
    	    	
    	return rez;
    }
}
