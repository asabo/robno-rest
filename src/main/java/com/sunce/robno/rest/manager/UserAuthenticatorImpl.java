package com.sunce.robno.rest.manager;

import javax.inject.Inject;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.net.Korisnik;


public class UserAuthenticatorImpl implements UserAuthenticator {
  private com.ansa.dao.DAOFactory daoFactory; 
    
    @Inject
    public UserAuthenticatorImpl(DAOFactory daoFactory) {
    	this.daoFactory = daoFactory;
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
                + "username: " + kor.getUsername() + " ID: " + kor.getUserId()
                );
       
    return daoFactory.izmjeniLozinku(kor);
    }
    
}
