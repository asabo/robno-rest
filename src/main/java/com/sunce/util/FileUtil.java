package com.sunce.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class FileUtil {
	private static Logger LOG = LogManager.getLogger(FileUtil.class);

	public static InputStream findConfFileInCommonSpace(String fileName) {
		try {

			LOG.info("Trying to find conf file in common space.. fname: " + fileName);
		    //Use Any Environmental Variable , here i have used CATALINA_HOME
		    String propertyHome = System.getenv("CATALINA_HOME");           
			LOG.info("Trying to find conf file in common space, catalina home:" + propertyHome );

		    if(null == propertyHome){
		        //This is a system property that is  passed
		        // using the -D option in the Tomcat startup script
		        propertyHome  =  System.getProperty("PROPERTY_HOME");
				LOG.info("Since catalina home not set, trying with property home:" + propertyHome );
		    }
		    
			if (propertyHome == null) {
				//zadnja opcija u istom folderu gdje je aplikacija... 
				propertyHome = System.getProperty("user.dir");
			}
			
			String filePath = propertyHome + "/properties/" + fileName;
			LOG.info("Trying to open properties file:" + filePath);
         
		    InputStream resourceAsStream = new FileInputStream(filePath);
			return resourceAsStream;
		} catch (Exception e) {
		    LOG.error("Problem finding conf file in common space", e);
		    return null;
		}
	}
}
