package com.sunce.robno.rest;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunce.util.PropertiesReader;

/*
 * sva konfiguracija vezana za aplikaciju, spojevi na baze, vanjske ovisnosti i sl.
 * svaki konfiguracijski objekt mora izaci u vidu nekakvog beana, kako bi ga se moglo injektirati 
 * drugim objektima kojima je potreban, a u isto vrijeme testove olaksati uvaljivanjem nekih drugih konfiguracija
 * klasama koje trebaju biti ili mockane ili raditi svoj posao na nekim privremenim resursima
 */
public final class AppConfig {
	private static Logger LOG = LogManager.getLogger();

	public static String TEST_ENV = "test";
	public static String PROD_ENV = "prod";

	public static String ENV = TEST_ENV;

	public static String VERSION = "1.1.6_" + ENV;

	public static String propFileStr = "/nika_" + ENV + ".properties";
	public static String emailConfigFileStr = "/nika_device_mapping_" + ENV + ".csv";
	
	 
	private static MailConfig mailCnf 	= null;
	
	static Properties DB_PROPERTIES = PropertiesReader.getInstance().getProperties(propFileStr);
	
	private static void init() {

		String address 	= DB_PROPERTIES.getProperty("mysql.address");
		String user 	= DB_PROPERTIES.getProperty("mysql.username");
		String password = DB_PROPERTIES.getProperty("mysql.password");
		String repo 	= DB_PROPERTIES.getProperty("mysql.db");
		String portStr 	= DB_PROPERTIES.getProperty("mysql.port");
		String encoding = DB_PROPERTIES.getProperty("mysql.encoding");

		LOG.info("Connection to DB initiated, address: " + address + " user: " + user + " db: " + repo + " port: "
				+ portStr);

		int port = Integer.valueOf(portStr);
	}
	
	public static MailConfig getMailCnf() {
		if (mailCnf == null) {
			mailCnf = new MailConfig();
			mailCnf.setAuth(Boolean.valueOf(DB_PROPERTIES.getProperty("mail.smtp.auth","false")));
			mailCnf.setCrashReportReceivers(DB_PROPERTIES.getProperty("mail.crash_report_receivers"));
			mailCnf.setHost(DB_PROPERTIES.getProperty("mail.smtp.host"));
			mailCnf.setOrderReceivers(DB_PROPERTIES.getProperty("mail.order_receivers"));
			mailCnf.setPassword(DB_PROPERTIES.getProperty("mail.smtp.password"));
			mailCnf.setPort(Integer.valueOf(DB_PROPERTIES.getProperty("mail.smtp.port","25")));
			mailCnf.setUsername(DB_PROPERTIES.getProperty("mail.smtp.user"));
			mailCnf.setFrom(DB_PROPERTIES.getProperty("mail.smtp.from"));
			mailCnf.setReplyTo(DB_PROPERTIES.getProperty("mail.smtp.replyto"));
			mailCnf.setProtocol(DB_PROPERTIES.getProperty("mail.smtp.protocol","smtp"));
			mailCnf.setStartTLS(Boolean.valueOf(DB_PROPERTIES.getProperty("mail.smtp.starttls.enable","false")));
			mailCnf.setTestMode(ENV == TEST_ENV);
		}
		
		return mailCnf;
	}
	  
}
