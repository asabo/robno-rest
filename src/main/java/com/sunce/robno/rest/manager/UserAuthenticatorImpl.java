package com.sunce.robno.rest.manager;

import javax.inject.Inject;

import com.sunce.robno.rest.RobnoRestApp;
import com.sunce.robno.rest.dto.UserCredentials;

/**
 * class in charge to hold logic that will solve problem of paint 
 * optimizations. It may have injected different dependencies and work with 
 * different implementation solution providers, but it is imagined as a central 
 * place for solving a problem of paint optimizations.
 */
public class UserAuthenticatorImpl implements UserAuthenticator {
  private com.ansa.dao.DAOFactory daoFactory; 
    
    @Inject
    public UserAuthenticatorImpl() {
    	this.daoFactory = RobnoRestApp.getDAOFactory();
    }
      
    @Override
    public int authenticateUser(UserCredentials creds) {
        System.out.println("We got task to authenticate user " 
                + "username: " + creds.getUsername() 
                );
       
    return daoFactory.logirajKorisnika(creds.getUsername(), creds.getPassword());
    }
    
}
