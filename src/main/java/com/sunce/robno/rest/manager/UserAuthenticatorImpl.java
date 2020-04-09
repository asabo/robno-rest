package com.sunce.robno.rest.manager;

import javax.inject.Inject;

import com.ansa.dao.net.Korisnik;
import com.sunce.robno.rest.RobnoRestApp;

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
    public Korisnik authenticateUser(Korisnik kor) {
        System.out.println("We got task to authenticate user " 
                + "username: " + kor.getUsername() 
                );
       
    return daoFactory.logirajKorisnika(kor);
    }
    
    @Override
    public Boolean changePassword(Korisnik kor) {
        System.out.println("We got task to change password for user " 
                + "username: " + kor.getUsername() 
                );
       
    return daoFactory.izmjeniLozinku(kor);
    }
    
}
