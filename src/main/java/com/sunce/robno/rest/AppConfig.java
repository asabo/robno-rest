package com.sunce.robno.rest;

import java.util.Properties;

import com.sunce.util.PropertiesReader;

/*
 * sva konfiguracija vezana za aplikaciju, spojevi na baze, vanjske ovisnosti i sl.
 * svaki konfiguracijski objekt mora izaci u vidu nekakvog beana, kako bi ga se moglo injektirati 
 * drugim objektima kojima je potreban, a u isto vrijeme testove olaksati uvaljivanjem nekih drugih konfiguracija
 * klasama koje trebaju biti ili mockane ili raditi svoj posao na nekim privremenim resursima
 */
public final class AppConfig {

	public static String TEST_ENV = "test";
	public static String PROD_ENV = "prod";

	public static String ENV = TEST_ENV;
 
	public static String propFileStr = "/nika_" + ENV + ".properties";
	 
 
	private static MailConfig mailCnf 	= null;
	
	static Properties DB_PROPERTIES = PropertiesReader.getInstance().getProperties(propFileStr);
	
	
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
