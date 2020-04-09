package com.sunce.robno.rest.resource.v1;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ansa.dao.net.Rezultat;
import com.ansa.dao.net.Upit;
import com.sunce.robno.rest.manager.DaoFactoryManager;

@Path("v1/daoFactory")
@Produces(MediaType.APPLICATION_JSON)
public class DaoFactory {

    DaoFactoryManager daoFactoryManager;

    @Inject
    public DaoFactory(DaoFactoryManager daoFactoryManager) {
        this.daoFactoryManager = daoFactoryManager;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/performQuery")
    public Rezultat performQuery(Upit query) { 
    	return this.daoFactoryManager.performQuery(query);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/performUpdate")
    public int performUpdate(Upit upit) { 
    	return this.daoFactoryManager.performUpdate(upit);
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/isTableEmpty")
    public boolean isTableEmpty(@QueryParam("name") String tableName) { 
    	return this.daoFactoryManager.isTableEmpty(tableName);
    }
        
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/nextAvailableId")
    public int nextAvailableId(@QueryParam("name") String tableName, @QueryParam("key") String keyName) { 
    	return this.daoFactoryManager.nextAvailableKey(tableName, keyName);
    }
}
