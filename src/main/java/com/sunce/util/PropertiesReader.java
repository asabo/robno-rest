package com.sunce.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
 

public final class PropertiesReader {
	private static Logger LOG = LogManager.getLogger(PropertiesReader.class);
	private static PropertiesReader instance = null;

	private Hashtable<String, Properties> propertiesHash = new Hashtable<String, Properties>();

	private Hashtable<String, Long> propertiesLastModifiedHash = new Hashtable<String, Long>();

	private PropertiesReader() {
	}

	public static PropertiesReader getInstance() {
		if (instance == null) {
			instance = new PropertiesReader();
		}
		return instance;
	}

	public Properties getProperties(String propFileStr) {
		Long datePropFileModified = (Long) this.propertiesLastModifiedHash.get(propFileStr);

		URL url = this.getClass().getResource(propFileStr);

		if (url == null) {
			LOG.info("Property file " + propFileStr + " ne postoji, trazit cemo ga u common spaceu...");
		}
		
	
		File propFile = null;
		Properties prop = (Properties) this.propertiesHash.get(propFileStr);
		try {
			propFile = url==null ? null : new File(url.toString());

			Long lastModified = url==null ? System.currentTimeMillis() : new Long(propFile.lastModified());

			if (prop == null || datePropFileModified != null && !datePropFileModified.equals(lastModified)) {

				synchronized (this) {

					InputStream is = url==null ? FileUtil.findConfFileInCommonSpace(propFileStr) : url.openStream();
					
					if (is==null) {
						LOG.error("IS je null, vracamo null");
						return null;
					}
					
					prop = new Properties();
					try {
						prop.load(is);
					} catch (IOException e) {
						LOG.info("IOEx kod citanja " + propFileStr + " last modified: " + lastModified 
								+ " message: " + e.getMessage(), e);

						throw new RuntimeException(e);
					}
					try {
						if (is!=null) is.close();
					} catch (IOException e) {
						LOG.error("IOEx kod zatvaranja streama: "+e.getMessage(), e);
					}
				}

				this.propertiesHash.put(propFileStr, prop);
				this.propertiesLastModifiedHash.put(propFileStr, lastModified);
			}
		} catch (IOException e) {
			LOG.error("IOEx kod citanja property datoteke: "+e.getMessage(), e);
			throw new RuntimeException("Greska pri pristupu property datoteci: " + propFileStr, e);
		}

		return prop;
	}

	/**
	 * pretvara string formata  kljuc1=vrijednost1;kljuc2=vrijednost2;... u Properties objekt
	 * @param props
	 * @return
	 */
	public Properties fromString(String props)
	{
		if (StringUtils.isEmpty(props))
			return null;
		
		Properties prop = new Properties();
		ByteArrayInputStream bains = null; 
		
		try
		{
			bains = new ByteArrayInputStream(props.replaceAll(";", "\n").getBytes());
			prop.load(bains);
			
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try{if (bains!=null) bains.close();}catch(Exception e){} bains=null;
		}
		
		return prop;
	}

	
	
}