package com.sunce.robno.rest.manager;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.ansa.dao.DAOObjekt;
import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Kolona;
import com.sunce.robno.rest.RobnoRestApp;

/**
 * vraca podatke o nekom dao objektu
 */
public class DaoObjektManagerImpl implements DaoObjektManager {
  private com.ansa.dao.DAOFactory daoFactory; 
    
    @Inject
    public DaoObjektManagerImpl() {
    	this.daoFactory = RobnoRestApp.getDAOFactory();
    }

	@Override
	public Set<Kolona> getTableColumns(String name) {
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
		return daoObjekt.getTableColumns(name);
	}

	@Override
	public List<Kolona> getTablePrimaryKey(String name) {
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
		return daoObjekt.getTablePrimaryKey(name);
	}

	@Override
	public List<Kolona> getTableImportedKeys(String name) {
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
		return daoObjekt.getImportedKeys(name);
	}
	
	@Override
	public ValueObject readObject(String name, int id) {
		if (id<1)
			throw new IllegalArgumentException("ID param out of range");
		
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
	    try {
			ValueObject obj = (ValueObject) daoObjekt.read(Integer.valueOf(id));
			return obj;
		} catch (SQLException e) {
			System.out.println("Problem reading object, name: " + name + " ID: " + id + " E: " + e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean insertObject(String name, ValueObject object) {
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
	    try {
			daoObjekt.insert(object);
		} catch (SQLException e) {
			System.out.println("Problem inserting object: " + object + " E: " + e);
			return false;
		}
	    return object.getStatus() == 'I';
	}

	@Override
	public boolean updateObject(String name, ValueObject object) {
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
	    try {
			return daoObjekt.update(object);
		} catch (SQLException e) {
			System.out.println("Problem updating object: " + object + " E: " + e);
			return false;
		}
	}

	@Override
	public boolean deleteObject(String name, int id) {
		if (id<1) 
			throw new IllegalArgumentException("ID param out of range!");
		DAOObjekt daoObjekt = this.daoFactory.getDAOObjekt(name);
	    try {
			daoObjekt.delete(Integer.valueOf(id));
			return true;
		} catch (SQLException e) {
			System.out.println("Problem deleting object, ID: " + id + " E: " + e);
			return false;
		}
		
	}
}
