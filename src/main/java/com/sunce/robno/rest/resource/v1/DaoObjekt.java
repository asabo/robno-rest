package com.sunce.robno.rest.resource.v1;

import java.util.List;
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

import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Kolona;
import com.sunce.robno.rest.manager.DaoObjektManager;

@Path("v1/daoObject")
@Produces(MediaType.APPLICATION_JSON)
public class DaoObjekt {

    DaoObjektManager daoObjektManager;

    @Inject
    public DaoObjekt(DaoObjektManager daoObjektManager) {
        this.daoObjektManager = daoObjektManager;
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/columns")
    public Set<Kolona> getTableColumns(@QueryParam("name") String name) { 
    	return this.daoObjektManager.getTableColumns(name);            	
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/primaryKeys")
    public List<Kolona> getTablePrimaryKeys(@QueryParam("name") String name) { 
    	return this.daoObjektManager.getTablePrimaryKey(name);            	
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importedKeys")
    public List<Kolona> getTableImportedKeys(@QueryParam("name") String name) { 
    	return this.daoObjektManager.getTableImportedKeys(name);            	
    }
    

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public ValueObject read(@QueryParam("name") String name, @QueryParam("id") Integer id) { 
    	return this.daoObjektManager.readObject(name, id);            	
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public boolean insertObject(@QueryParam("name") String name, ValueObject object) { 
    	return this.daoObjektManager.insertObject(name, object);            	
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public boolean updateObject(@QueryParam("name") String name, ValueObject object) { 
    	return this.daoObjektManager.updateObject(name, object);            	
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public boolean deleteObject(@QueryParam("name") String name, @QueryParam("id") int id) { 
    	return this.daoObjektManager.deleteObject(name, id);            	
    }
    
}
