package com.sunce.robno.rest.resource.v1;

import java.io.IOException;
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

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.SearchCriteriaObject;
import com.ansa.dao.ValueObject;
import com.ansa.dao.net.InternalServerErrorException;
import com.ansa.dao.net.Kolona;
import com.ansa.dao.net.Rezultat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunce.robno.rest.manager.DaoObjektManager;
import com.sunce.util.ZipUtil;

@Path("v1/daoObject")
@Produces(MediaType.APPLICATION_JSON)
public class DaoObjekt {
	private final static Logger LOG = Log.getLogger(DaoObjekt.class);

    DaoObjektManager daoObjektManager;
    ObjectMapper mapper;

    @Inject
    public DaoObjekt(DaoObjektManager daoObjektManager, ObjectMapper mapper) {
        this.daoObjektManager = daoObjektManager;
        this.mapper = mapper;
    }
    
    private Map<String, Set<Kolona>> columnsCache = new HashMap<>();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/columns")
    public byte[] getTableColumns(@QueryParam("name") String name,  @QueryParam("gzip") Boolean gzip) {
    	Set<Kolona> cols;
    	if (columnsCache.containsKey(name)) 
    		cols = columnsCache.get(name);
    	else {
    	    cols = this.daoObjektManager.getTableColumns(name);
    		columnsCache.put(name, cols);
    	} 
    	
       	try {
    			byte[] resBytes = mapper.writeValueAsBytes( cols );
    			
    			resBytes = gzip!=null && gzip.booleanValue() ? ZipUtil.compress( resBytes ) : resBytes;
    			
    			return resBytes;
    		} catch (JsonProcessingException e) {
    			throw new InternalServerErrorException("Problem while processing incoming stream (columns)", e, 500);
    		}
    }
    
    private Map<String, List<Kolona>> primKeysCache = new HashMap<>();
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/primaryKeys")
    public byte[] getTablePrimaryKeys(@QueryParam("name") String name,  @QueryParam("gzip") Boolean gzip) {
    	List<Kolona> keys = null; 
    	
    	if (primKeysCache.containsKey(name))
    		keys = primKeysCache.get(name);
    	else {
    	 keys = this.daoObjektManager.getTablePrimaryKey(name);
    	 primKeysCache.put(name, keys);
    	} 
    	
    	try {
			byte[] resBytes = mapper.writeValueAsBytes(keys);
			
			resBytes = gzip!=null && gzip.booleanValue() ? ZipUtil.compress(resBytes) : resBytes;
			
			return resBytes;
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Problem while processing incoming stream (primaryKeys)", e, 500);
		}
    }
    
    private Map<String, List<Kolona>> importedKeysCache = new HashMap<>();
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/importedKeys")
    public byte[] getTableImportedKeys(@QueryParam("name") String name, @QueryParam("gzip") Boolean gzip) { 
    	List<Kolona> impKeys;
    	
    	if (importedKeysCache.containsKey(name))
    		impKeys = importedKeysCache.get(name);
    	else {    		
    		impKeys = this.daoObjektManager.getTableImportedKeys(name);
    		importedKeysCache.put(name, impKeys);
    	}
    	
    	try {
			byte[] resBytes = mapper.writeValueAsBytes(impKeys);
			
			resBytes = gzip!=null && gzip.booleanValue() ? ZipUtil.compress(resBytes) : resBytes;
			
			return resBytes;
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Problem while processing incoming stream (importedKeys)", e, 500);
		}
    }
    

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public byte[] read(@QueryParam("name") String name, @QueryParam("id") String id, @QueryParam("gzip") Boolean gzip) { 
    	if(id == null ) return null;
    	
    	ValueObject rez = this.daoObjektManager.readObject(name, id);
    	    	
    	try {
			byte[] resBytes = mapper.writeValueAsBytes(rez);
			
			resBytes = gzip!=null && gzip.booleanValue() ? ZipUtil.compress(resBytes) : resBytes;
			
			return resBytes;
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Problem while processing incoming stream", e, 500);
		}
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/findAll")
    public byte[] readAll(@QueryParam("name") String name, byte[] scoKey, @QueryParam("gzip") Boolean gzip) { 
    	
    	SearchCriteriaObject key; 
    	
    	if (gzip!=null && gzip.booleanValue()) {
    		scoKey = ZipUtil.uncompress( scoKey );
    	}
    	
    	try {
			key = mapper.readValue(scoKey, SearchCriteriaObject.class);
		} catch (IOException e) {
			LOG.warn("Problem kod konverzije ulaznog streama podataka u SearchCriteriaObject, gzip: " + gzip + " name: " + name, e);
			throw new InternalServerErrorException("Problem while converting incoming stream", e, 500);
		}
    	
    	Rezultat rez = this.daoObjektManager.readAll(name, key);
    	    	    	
    	try {
			byte[] resBytes = mapper.writeValueAsBytes(rez);
			
			resBytes = gzip!=null && gzip.booleanValue() ? ZipUtil.compress(resBytes) : resBytes;
			
			return resBytes;
		} catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Problem while processing incoming stream", e, 500);
		}
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public byte[] insertObject(@QueryParam("name") String name,  @QueryParam("gzip") Boolean gzip, byte[] obj) {
    	ValueObject object;
    	
    	if (gzip!=null && gzip.booleanValue()) {
    		obj = ZipUtil.uncompress( obj );
    	}
    	
    	try {
			object = mapper.readValue(obj, ValueObject.class);
		} catch (IOException e) {
			LOG.warn("Problem kod konverzije ulaznog streama podataka u ValueObject, gzip: " + gzip + " name: " + name, e);
			throw new InternalServerErrorException("Problem while converting incoming stream", e, 500);
		}
    	
    	int res = this.daoObjektManager.insertObject(name, DAOFactory.adaptirajValueObject(object));
    	
    	byte[] barr = ("" + res).getBytes();
    	
    	return gzip ? ZipUtil.compress( barr ) : barr;
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
