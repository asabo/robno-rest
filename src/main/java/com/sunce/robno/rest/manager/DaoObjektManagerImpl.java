package com.sunce.robno.rest.manager;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.DAOObjekt;
import com.ansa.dao.Logger;
import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Kolona;
import com.ansa.dao.net.Rezultat;

/**
 * vraca podatke o nekom dao objektu
 */
public class DaoObjektManagerImpl implements DaoObjektManager {
 
  private DaoObjectCacheManager cache;
  private DAOFactory daoFactory;
    
    @Inject
    public DaoObjektManagerImpl(DAOFactory daoFactory) {
    	 this.daoFactory = daoFactory;
    	 cache = new DaoObjectCacheManager();
    }

	@Override
	public Set<Kolona> getTableColumns(String name) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
		return daoObjekt.getTableColumns(name);
	}

	@Override
	public List<Kolona> getTablePrimaryKey(String name) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
		return daoObjekt.getTablePrimaryKey(name);
	}

	@Override
	public List<Kolona> getTableImportedKeys(String name) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
		return daoObjekt.getImportedKeys(name);
	}
	
	@Override
	public ValueObject readObject(String name, String id) {
		return cache.readObject(name, id);
	}
	
	@Override
	public Rezultat readAll(String name, Object key) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
	    try {
	    	List<ValueObject> podaci = daoObjekt.findAll(key, false);
	    	
	    	Rezultat rez = new Rezultat(); 
	    	rez.setPodaci(podaci);
	    	rez.setVelicina(podaci.size());
	    	rez.setKolone(daoObjekt.getKolone());
	    	rez.setDescriptori(daoObjekt.getDescriptori());
	    	rez.setTipoviKolona(daoObjekt.getTipoviKolona());
	    	
	    	return rez;
	    } catch(SQLException sqle) {
	       Logger.log("Problem reading all objects, name: " + name + " key: " + key, sqle);
		  throw new RuntimeException(sqle);
	    }
	}

	@Override
	public int insertObject(String name, ValueObject object) {
		if (object == null || StringUtils.isEmpty(name)) {
			Logger.log("Empty objects cannot be inserted obj: " + object + " name: " + name);
			return -1;
		}
			
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
	    try {
			daoObjekt.insert(object);
			cache.addObject(name, object); //vjerojatno ce ga se odmah traziti, nek sjedi u cacheu
		} catch (SQLException e) {
			Logger.log("Problem inserting object: " + object , e);
			return -1;
		}
	    
	    return object.getStatus() == 'I' ? object.getSifra() : -1;
	}

	@Override
	public boolean updateObject(String name, ValueObject object) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
	    try {
	    	cache.removeObject(name, object);
			return daoObjekt.update(object);
		} catch (SQLException e) {
			Logger.log("Problem updating object: " + object, e);
			return false;
		}
	}

	@Override
	public boolean deleteObject(String name, int id) {
		if (id<1) 
			throw new IllegalArgumentException("ID param out of range!");
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
	    try {
	    	Integer objId = Integer.valueOf(id);
			cache.removeObject(name, objId);
			daoObjekt.delete(objId);
			return true;
		} catch (SQLException e) {
			Logger.log("Problem deleting object, ID: " + id, e);
			return false;
		}
	}
}
