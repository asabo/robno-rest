package com.sunce.robno.rest.resource.v1;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.ansa.dao.net.InternalServerErrorException;
import com.ansa.dao.net.Rezultat;
import com.ansa.dao.net.Upit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunce.robno.rest.manager.DaoFactoryManager;
import com.sunce.util.ZipUtil;

@Path("v1/daoFactory")
@Produces(MediaType.APPLICATION_JSON)
public class DaoFactory {
	private static final Logger LOG = Log.getLogger(DaoFactory.class);

    DaoFactoryManager daoFactoryManager;
    ObjectMapper mapper;

    @Inject
    public DaoFactory(DaoFactoryManager daoFactoryManager, ObjectMapper mapper) {
        this.daoFactoryManager = daoFactoryManager;
        this.mapper = mapper;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/performQuery")
    public byte[] performQuery(@QueryParam("gzip") boolean gzip, byte[] queryBytes) {
    	Upit query = null;
    	
    	if (gzip) {
    		queryBytes = ZipUtil.uncompress(queryBytes);
    	}
    	
    	try {
			query = mapper.readValue(queryBytes, Upit.class);
		} catch (IOException e) {
			LOG.warn("Problem kod konverzije ulaznog streama podataka u Upit", e);
			throw new InternalServerErrorException("Problem while converting incoming stream", e, 500);
		}
    	
    	Rezultat rez = this.daoFactoryManager.performQuery(query);
    	
    	try {
			byte[] resBytes = mapper.writeValueAsBytes(rez);
			
			resBytes = gzip ? ZipUtil.compress(resBytes) : resBytes;
			
			return resBytes;
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Problem while processing incoming stream", e, 500);
		}
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/performUpdate")
    public byte[] performUpdate(@QueryParam("gzip") boolean gzip, byte[] queryBytes) { 
    	Upit query = null;
    	
    	if (gzip) {
    		queryBytes = ZipUtil.uncompress(queryBytes);
    	}
    	
    	try {
			query = mapper.readValue(queryBytes, Upit.class);
		} catch (IOException e) {
			LOG.warn("Problem kod konverzije ulaznog streama podataka u Upit", e);
			throw new InternalServerErrorException("Problem while converting incoming stream", e, 500);
		}
    	
    	int res = this.daoFactoryManager.performUpdate(query);
    
    	byte[] barr = ("" + res).getBytes();
    	
    	return gzip ? ZipUtil.compress( barr ) : barr;
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
