package com.sunce.robno.rest.resource.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.SearchCriteriaObject;
import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Kolona;
import com.ansa.dao.net.Rezultat;
import com.sunce.robno.rest.manager.DaoObjektManager;

@Path("v1/daoObject")
@Produces(MediaType.APPLICATION_JSON)
public class DaoObjekt {

    DaoObjektManager daoObjektManager;

    @Inject
    public DaoObjekt(DaoObjektManager daoObjektManager) {
        this.daoObjektManager = daoObjektManager;
    }
    
    private Map<String, Set<Kolona>> columnsCache = new HashMap<>();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/columns")
    public Set<Kolona> getTableColumns(@QueryParam("name") String name) {
    	if (columnsCache.containsKey(name)) 
    		return columnsCache.get(name);
    	else {
    	
    		Set<Kolona> cols = this.daoObjektManager.getTableColumns(name);
    		columnsCache.put(name, cols);
    		return cols;
    	}            	
    }
    
    private Map<String, List<Kolona>> primKeysCache = new HashMap<>();
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/primaryKeys")
    public List<Kolona> getTablePrimaryKeys(@QueryParam("name") String name) {
    	if (primKeysCache.containsKey(name))
    		return primKeysCache.get(name);
    	else {
    	 List<Kolona> keys = this.daoObjektManager.getTablePrimaryKey(name);
    	 primKeysCache.put(name, keys);
    	 return keys;
    	}            	
    }
    
    private Map<String, List<Kolona>> importedKeysCache = new HashMap<>();
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importedKeys")
    public List<Kolona> getTableImportedKeys(@QueryParam("name") String name) { 
    	if (importedKeysCache.containsKey(name))
    		return importedKeysCache.get(name);
    	else {    		
    		List<Kolona> keys = this.daoObjektManager.getTableImportedKeys(name);
    		importedKeysCache.put(name, keys);
    		return keys;
    	}
    }
    

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public ValueObject read(@QueryParam("name") String name, @QueryParam("id") String id) { 
    	if(id == null ) return null;
    	
    	return this.daoObjektManager.readObject(name, id);            	
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/findAll")
    public Rezultat readAll(@QueryParam("name") String name, SearchCriteriaObject key) { 
    	return this.daoObjektManager.readAll(name, key);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public int insertObject(@QueryParam("name") String name, ValueObject object) { 
    	return this.daoObjektManager.insertObject(name, DAOFactory.adaptirajValueObject(object));            	
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public boolean updateObject(@QueryParam("name") String name, ValueObject object) { 
    	return this.daoObjektManager.updateObject(name, DAOFactory.adaptirajValueObject(object));            	
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public boolean deleteObject(@QueryParam("name") String name, @QueryParam("id") int id) { 
    	return this.daoObjektManager.deleteObject(name, id);            	
    }
    
}
