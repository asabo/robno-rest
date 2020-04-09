package com.sunce.robno.rest.manager;

import java.sql.SQLException;

import javax.inject.Inject;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.Logger;
import com.ansa.dao.net.Rezultat;
import com.ansa.dao.net.Upit;

public class DaoFactoryManagerImpl implements DaoFactoryManager {
	    
	 @Inject
	 public DaoFactoryManagerImpl() { 
	 }
	    
	@Override
	public Rezultat performQuery(Upit upit) {
		return DAOFactory.performQuery(upit);
	} 

	@Override
	public int performUpdate(Upit upit) {
		return DAOFactory.performUpdate(upit);
	}

	@Override
	public boolean isTableEmpty(String tableName) {
		try {
			return DAOFactory.isTableEmpty(tableName);
		} catch (SQLException e) {
			Logger.log("Iznimka kod pozivanja isTableEmpty()", e);
			throw new IllegalStateException("Exception while checking table emptiness: " + tableName);
		}
	}

	@Override
	public int nextAvailableKey(String tableName, String key) {
		try {
			return DAOFactory.vratiSlijedecuSlobodnuSifruZaTablicu(tableName, key);
		} catch (SQLException e) {
			Logger.log("Iznimka kod pozivanja isTableEmpty()", e);
			throw new IllegalStateException("Exception while checking table emptiness: " + tableName);
		}
	}

}
