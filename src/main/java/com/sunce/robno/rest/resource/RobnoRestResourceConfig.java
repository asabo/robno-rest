/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.resource;

import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.BeanParam;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.ansa.dao.DAOFactory;
import com.ansa.util.beans.PostavkeBeanOpce;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.sunce.robno.rest.manager.DaoFactoryManager;
import com.sunce.robno.rest.manager.DaoFactoryManagerImpl;
import com.sunce.robno.rest.manager.DaoObjektManager;
import com.sunce.robno.rest.manager.DaoObjektManagerImpl;
import com.sunce.robno.rest.manager.Konstante;
import com.sunce.robno.rest.manager.MailManager;
import com.sunce.robno.rest.manager.MailManagerImpl;
import com.sunce.robno.rest.manager.StockManager;
import com.sunce.robno.rest.manager.StockManagerImpl;
import com.sunce.robno.rest.manager.UserAuthenticator;
import com.sunce.robno.rest.manager.UserAuthenticatorImpl;


/**
 *
 * @author ante
 */
public class RobnoRestResourceConfig extends ResourceConfig {

	private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

	public RobnoRestResourceConfig() {

		// where the resource classes are

		packages("com.sunce.robno.rest.resource");

		setApplicationName("robno-rest");

		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(UserAuthenticatorImpl.class).to(UserAuthenticator.class);
				bind(DaoObjektManagerImpl.class).to(DaoObjektManager.class);
				bind(DaoFactoryManagerImpl.class).to(DaoFactoryManager.class);
				bind(MailManagerImpl.class).to(MailManager.class);
				bind(StockManagerImpl.class).to(StockManager.class).in(Singleton.class);
				//bind(CustomObjectMapper.class).to(ObjectMapper.class).in(Singleton.class);
				bind(getDAOFactory()).to(DAOFactory.class);
				bind(jacksonObjectMapper()).to(ObjectMapper.class);
			}
		});
	}

	private static Properties props = null;

	@Resource
	@Singleton
	public final Properties properties() {
		if (props == null) {
			String propFileStr = "robno.properties";
			props = com.sunce.util.PropertiesReader.getInstance().getProperties(propFileStr);
		}

		return props;
	}

	private static DAOFactory factory;

	@Resource
	@Singleton
	public final static DAOFactory getDAOFactory() {
		if (factory != null)
			return factory;

		factory = DAOFactory.getInstance();
		String sep = System.getProperty("file.separator");
		PostavkeBeanOpce post = new PostavkeBeanOpce();

		String server = post.getDbServer();
		if (server == null)
			server = "127.0.0.1";

		System.out.println("(REST) Spajanje na bazu: " + server + " driver: " + MYSQL_DRIVER + " Baza: "
				+ Konstante.BAZA_PODATAKA);

		factory.connectToDB(MYSQL_DRIVER, "jdbc:mysql://", server,
				Konstante.BAZA_PODATAKA + Konstante.BAZA_DEF_ENCODING, Konstante.BAZA_USERNAME, Konstante.BAZA_PASSWORD,
				Konstante.MIN_DB_VEZA_POOL, Konstante.MAX_DB_VEZA_POOL,
				vratiKonfiguracijskiDirektorijKorisnika() + sep + Konstante.DB_LOG_FILE, 1.0d);

		return factory;
	}// getDAOFactory

	public final static String vratiKonfiguracijskiDirektorijKorisnika() {
		String uHome = "."; //System.getProperty("user.home");
		String sep = System.getProperty("file.separator");
		String dir = uHome + sep + "logs";

		return dir;
	}// vratiKonfiguracijskiDirektorijKorisnika

	private static CustomObjectMapper objMapper = null;
	
	@BeanParam
	public ObjectMapper jacksonObjectMapper() {
		if (objMapper == null) 
			objMapper = new CustomObjectMapper();
		
		return objMapper;
	}

	@BeanParam
	public SerializationConfig serializationConfig() {
		return jacksonObjectMapper().getSerializationConfig();
	}

}

class CustomObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = -8464818847000721314L;

	public CustomObjectMapper() {
		//this.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// this.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
		// true);
	}
}