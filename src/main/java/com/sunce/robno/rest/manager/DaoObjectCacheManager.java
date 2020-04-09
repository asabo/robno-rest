package com.sunce.robno.rest.manager;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.NotFoundException;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.DAOObjekt;
import com.ansa.dao.Logger;
import com.ansa.dao.ValueObject;

public class DaoObjectCacheManager {
	
	Set<String> cachedEntities = new HashSet<String>();
	Map<String, ValueObject> cache = new ConcurrentHashMap<>();
	
	public DaoObjectCacheManager() {
		cachedEntities.add("katalog.jedinica_mjere");
		cachedEntities.add("katalog.artikl");
		cachedEntities.add("katalog.barcode");
		cachedEntities.add("katalog.drzava");
		cachedEntities.add("katalog.mjesto");
		cachedEntities.add("katalog.porezna_stopa");
		cachedEntities.add("katalog.poslovnica");
		cachedEntities.add("katalog.proizvodjac");
		cachedEntities.add("katalog.skladiste");
		cachedEntities.add("katalog.tip_racuna");
		cachedEntities.add("katalog.valuta");
		cachedEntities.add("katalog.vrsta_placanja");
		cachedEntities.add("katalog.ziro_racun");
		cachedEntities.add("katalog.grupa_proizvoda");
		cachedEntities.add("katalog.klasa_proizvoda");
		cachedEntities.add("katalog.blagajna");
		cachedEntities.add("katalog.banka");
		cachedEntities.add("katalog.dodijeljene_uloge");
		cachedEntities.add("postavke");

	}
	

	public ValueObject readObject(String name, String id) {
		if (id == null || "null".equals(id))
			throw new IllegalArgumentException("ID must be specified! name: " + name);
		
		Integer intId = null; 
		
		try {
		intId = Integer.valueOf( id );
		
		if (intId<1)
			throw new IllegalArgumentException("ID param out of range: " + id + " for object: " + name);
		} catch(NumberFormatException nfe) {			
		}
		
		String cacheId = name + id; 
	    try {
	    	if (cachedEntities.contains(name) && cache.containsKey(cacheId)) 
	    		return cache.get(cacheId);
	    	
	    	DAOObjekt daoObjekt = DAOFactory.getDAOObjekt(name);
			ValueObject obj =  daoObjekt.read(intId == null ? id : intId);
			if (obj == null)
				throw new NotFoundException("Object with ID " + id + " of '" + name + "' does not exist");
			
			cache.put(cacheId, obj);
			return obj;
		} catch (SQLException e) {
		  Logger.log("Problem reading object, name: " + name + " ID: " + id , e);
		  throw new RuntimeException(e);
		} catch (Exception e) {
			System.out.println("Generic exception while reading by id, cacheId: " + cacheId + " E: " + e);
			throw new RuntimeException(e);
		}
	}


	public void removeObject(String name, ValueObject object) {
		removeObject(name, object.getSifra());
	}
	
	public void removeObject(String name, Object objId) {
		String cacheId = name + objId; 
	 
	    	if (cachedEntities.contains(name) && cache.containsKey(cacheId)) 
	    		cache.remove(cacheId);
	    	
	}
	
	public void addObject(String name, ValueObject object) {
		addObject(name, object.getSifra(), object);
	}
	
	public void addObject(String name, Object objId, ValueObject obj) {
		String cacheId = name + objId; 
	 
	    	if (cachedEntities.contains(name)) 
	    		cache.put(cacheId, obj);
	}
}
