package com.sunce.web.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansa.dao.ValueObject;
import com.ansa.dao.net.EmailDTO;
import com.google.common.base.Strings;
import com.sunce.robno.rest.AppConfig;
import com.sunce.robno.rest.MailConfig;
import com.sunce.web.dto.ExceptionReportDTO;
import com.sunce.web.dto.OrderCompoundDTO;

public class OrderMailer {
	private final static Logger LOG = LogManager.getLogger();

	private MailConfig config;

	private String[] toOrders; 
	private String[] toCrashReport;
	private String[] replyTo;

	private SimpleDateFormat SDF;
	
	public OrderMailer() {
		this.config = AppConfig.getMailCnf();
		this.toOrders = toArrayEmails(config.getOrderReceivers());
		this.toCrashReport = toArrayEmails(config.getCrashReportReceivers());
		this.replyTo = toArrayEmails(config.getReplyTo());
		this.SDF = new SimpleDateFormat("dd/MM/yy hh:mm", Locale.UK);
	}

	// http://bc01.in3x.io/rest/
	private static final String HOST = "http://rest-sunce.rhcloud.com";

	public boolean sendOrderEmail(OrderCompoundDTO orderCompound, long acceptedOnServer, Boolean forced) {
		boolean res = false;

		LOG.info("Trying to send order email forced: " + forced);

		if (orderCompound == null) {
			return false;
		}

		ValueObject order = orderCompound.getOrder();
		List<ValueObject> articles = orderCompound.getArticles();
		
		String operatorId = orderCompound.getUsername();
		
		//File orderCsvFile = CsvFileWriter.writeOrderCsvFile(orderCompound, workingTmpDir);

		//LOG.info("Got TXT file: " + orderCsvFile.getAbsolutePath());

		String from = config.getFrom();
		String testMode = config.isTestMode() ? "[TEST] " : "";

		String subject = testMode + "Order #" + String.format("%06d", order.getId()) + 
				   (forced ? " [FORCED]":"");

		String message = generateOrderHtml(orderCompound, orderCompound.getVersion(), acceptedOnServer);

		Properties props = generateEmailProperties();
		String[] to = toOrders;
		
		try {
			//LOG.info("trysend");
			String fromName = null;
			String ccTo = null;
			 
			to = orderCompound.getDestinationEmails();
			fromName = orderCompound.getOperatorName();
			ccTo = orderCompound.getCcEmail();
			String[] bccTo = null;
			
			SendMail sendMail = new SendMail(fromName, from, to, ccTo, bccTo, replyTo, subject, message, props, config);
			
			String mailSendingMesg = "trysend inst, sending, from name: " + fromName + 
					" sender: " + from + " to: " + to.length +  " - "+ Arrays.toString(to) + " TLS: " + config.isStartTLS() +
					" port: " + config.getPort() + " protocol: " + config.getProtocol();
			LOG.info(mailSendingMesg );
				
			res = sendMail.send();
			// sendMail.sendSendgrid();
			LOG.info("trysend finished, mail sent: " + res);
			if (!res) {
				String mesg = "mail was NOT successfully sent! " + mailSendingMesg;
				//sendExceptionEmail(null, workingTmpDir, "(unknown)", mesg);
			}
			 
		} catch (Exception e) {
			String receivers = to == null ? "?!?" : Arrays.toString(to);
			String mesg = "Exception while sending order email, order #" + order.getId() + " to be sent to: "
					+ receivers;
			LOG.error(mesg, e);
			//sendExceptionEmail(e, workingTmpDir, "(unknown)", mesg);
			return false;
		} finally {
			//if (orderCsvFile != null)
			//	orderCsvFile.delete();
			props.clear();
			props = null;
		}

		return res;
	}
	
	public boolean sendInvoiceEmail(
			EmailDTO invoiceDto, ValueObject invoice, List<ValueObject> articles,
			ValueObject client,
			long acceptedOnServer, Boolean forced) {
		boolean res = false;
		
		if (invoiceDto == null) {
			return false;
		}
 		
		ValueObject invoicePdf = invoiceDto.getMeta();
		byte[] pdf = invoiceDto.getAttachment();
		
		String email = (String) invoicePdf.getValue("to");

		LOG.info("Trying to send invoice email to " + email + " forced: " + forced);

	
		//File orderCsvFile = CsvFileWriter.writeOrderCsvFile(orderCompound, workingTmpDir);

		//LOG.info("Got TXT file: " + orderCsvFile.getAbsolutePath());

		String from = config.getFrom();
		String testMode = config.isTestMode() ? "[TEST] " : "";
		String version = "1.0";

		String subject = testMode + invoicePdf.getValue("subject") + 
				   (forced ? " [FORCED]":"");

		String message = (String) invoicePdf.getValue("message");

		Properties props = generateEmailProperties();
		String[] to = new String[1]; to[0] = email;
		
		try {
			//LOG.info("trysend");
			String fromName = null;
			String ccTo = null;
			String bcc = (String) invoicePdf.getValue("bcc");
			String[] replyToAddress = null;
			 
			//to = config.getOrderReceivers().split(";");
			fromName = "Invoicing";
			ccTo = "";
			replyToAddress = this.replyTo;
			String invoiceName = "Invoice-"+invoice.getSifra()+".pdf"; 
			 
			String[] bccTo = bcc!=null ? bcc.split(";") : null;
			
			SendMail sendMail = new SendMail(fromName, from, to, ccTo, bccTo, replyToAddress, subject, message, props, config,
					pdf, invoiceName, "application/pdf");
			
			String mailSendingMesg = "trysend inst, sending invoice, from name: " + fromName + 
					" sender: " + from + " to: " + to.length +  " - "+ Arrays.toString(to) + " TLS: " + config.isStartTLS() +
					" port: " + config.getPort() + " protocol: " + config.getProtocol();
			LOG.info(mailSendingMesg );
				
			res = sendMail.send();
			// sendMail.sendSendgrid();
			LOG.info("trysend finished, invoice mail sent: " + res);
			if (!res) {
				String mesg = "mail was NOT successfully sent! " + mailSendingMesg;
				//sendExceptionEmail(null, workingTmpDir, "(unknown)", mesg);
			}
			 
		} catch (Exception e) {
			String receivers = to == null ? "?!?" : Arrays.toString(to);
			String mesg = "Exception while sending order email, invoice #" + invoice.getId() + " to be sent to: "
					+ receivers;
			LOG.error(mesg, e);
			//sendExceptionEmail(e, workingTmpDir, "(unknown)", mesg);
			return false;
		} finally {
			//if (orderCsvFile != null)
			//	orderCsvFile.delete();
			props.clear();
			props = null;
		}

		return res;
	} //sendInvoicePDF
	
	public boolean sendInvoiceEmailWithRRPPricesInExcel(
			byte[] invoiceExcel, ValueObject invoice, List<ValueObject> articles,
			ValueObject client,
			long acceptedOnServer, Boolean forced) {
		boolean res = false;
		
		String email = (String) client.getValue("EMAIL");

		LOG.info("Trying to send excel invoice email to " + email + " forced: " + forced);

		if (invoiceExcel == null) {
			return false;
		}
  

		String from = config.getFrom();
		String testMode = config.isTestMode() ? "[TEST] " : "";
		String version = "1.0";

		String subject = testMode + "Invoice #" + String.format("%06d", invoice.getValue("_ID2")) + 
				   (forced ? " [FORCED]":"");

		String message = generateInvoiceHtml(invoice, articles, version, acceptedOnServer, "Excel");

		Properties props = generateEmailProperties();
		String[] to = new String[1]; to[0] = email;
		
		try {
			//LOG.info("trysend");
			String fromName = null;
			String ccTo = null;
			String[] replyToAddress = null;
			 
			//to = config.getOrderReceivers().split(";");
			fromName = "Invoicing";
			ccTo = "";
			replyToAddress = this.replyTo;
			String invoiceName = "Invoice-"+invoice.getSifra()+".xls"; 
			String[] bcc = null;
			 
			SendMail sendMail = new SendMail(fromName, from, to, ccTo, bcc, replyToAddress, subject, message, props, config,
					invoiceExcel, invoiceName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			
			String mailSendingMesg = "trysend inst, sending invoice as excel, from name: " + fromName + 
					" sender: " + from + " to: " + to.length +  " - "+ Arrays.toString(to) + " TLS: " + config.isStartTLS() +
					" port: " + config.getPort() + " protocol: " + config.getProtocol();
			LOG.info(mailSendingMesg );
				
			res = sendMail.send();
			// sendMail.sendSendgrid();
			LOG.info("trysend finished, invoice mail sent: " + res);
			if (!res) {
				String mesg = "mail was NOT successfully sent! " + mailSendingMesg;
				//sendExceptionEmail(null, workingTmpDir, "(unknown)", mesg);
			}
			 
		} catch (Exception e) {
			String receivers = to == null ? "?!?" : Arrays.toString(to);
			String mesg = "Exception while sending order email, invoice #" + invoice.getId() + " to be sent to: "
					+ receivers;
			LOG.error(mesg, e);
			//sendExceptionEmail(e, workingTmpDir, "(unknown)", mesg);
			return false;
		} finally {
			//if (orderCsvFile != null)
			//	orderCsvFile.delete();
			props.clear();
			props = null;
		}

		return res;
	}


	private Properties generateEmailProperties() {
		Properties props = new Properties();

		//props.put("mail.smtp.starttls.enable", true); // added this line
		props.put("mail.smtp.host", config.getHost());
		props.put("mail.smtp.from", config.getFrom());
		props.put("mail.smtp.user", config.getUsername());
		props.put("mail.smtp.password", config.getPassword());
		props.put("mail.smtp.port", config.getPort());
		props.put("mail.smtp.auth", config.isAuth());
		props.put("mail.smtp.protocol", config.getProtocol());
		if (config.getProtocol().equals("smtps")) {
			props.put("mail.smtps.host", config.getHost());
			props.put("mail.smtps.from", config.getFrom());		
			props.put("mail.smtps.user", config.getUsername());
			props.put("mail.smtps.password", config.getPassword());
			props.put("mail.smtps.port", config.getPort());
			props.put("mail.smtps.auth", config.isAuth());			
		}
		props.put("mail.smtp.starttls.enable", ""+config.isStartTLS());
		if (config.isStartTLS()){
		 //props.put("mail.smtp.socketFactory.port", config.getPort());  
		 //props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
	     //props.put("mail.smtp.socketFactory.fallback", "false");  
		}

		props.put("kor", config.getUsername());

		// props.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		props.put("loza", config.getPassword());
		return props;
	}

	private static String[] toArrayEmails(String orderReceivers) {
		String[] emails = orderReceivers == null ? null : orderReceivers.split(";");
		return emails;
	}

	private  String generateOrderHtml(OrderCompoundDTO order, String version, long acceptedOnServer) {
		String html = "<HTML><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<link rel=\"stylesheet\" href=\"" + HOST + "/resources/css/style.css\" type=\"text/css\">"
				+ "<style type=\"text/css\">" + "	 table.round{ " + "	-moz-border-radius: 8px;"
				+ "	border-radius: 8px; " + "	}" + " table.lista tr.parni { background-color:silver; } "
				+ " table.lista tr.neparni { background-color:gray; }"
				+ " table.lista tr.greska { background-color:pink; color:red;  }"
				+ " </style> </head> <BODY > ";

		List<ValueObject> rez = order.getArticles();
		String sizeMesg = (rez == null ? "(no records)" : "" + rez.size());

		int articles = 0;
		if (rez!=null)
	  	 for (ValueObject vo : rez) {
			articles += vo.getIntValue("kolicina");
		 }
		
		String rh = "";
		 
			rh = "<tr><td><b>Operater: </b><td> " + order.getUsername();
		 
		String createdStr = SDF.format(new Date(order.getOrder().getCreated()));
		String closedStr = SDF.format(new Date(order.getOrder().getCreated()));
		String accepted = "";
		
		if (acceptedOnServer == -1) {
			createdStr = "<tr><td><b>: Accepted on server: </b><td> " + createdStr + "";
		} else {
			accepted = SDF.format(new Date(acceptedOnServer));
			createdStr = "<tr><td><b>Created on: </b><td> " + createdStr + "";
			accepted = "<tr><td><b>Accepted on server: </b><td> " + accepted+ "";
		}
		
		html += "<h1 align='center'>Order #" + String.format("%06d", order.getOrder().getSifra()) + "</h1> " + "<p align='left'> "
				+ "<table><tr><td><b>Customer ID: </b><td>" + order.getOrder().getValue("sifra_kupac") + ""
				+ "<tr><td><b>Operater ID: </b><td> " + order.getUsername() + "" 
				+ rh +
				createdStr +
				"<tr><td><b>Closed on: </b><td> " + closedStr + "" + 
				accepted 
				+ "<tr><td><b>Records: </b> <td>" + sizeMesg + "" + "<tr><td><b>articles: </b> <td>" + articles + ""
				+ "<tr><td><b>Environment: </b> <td>" + AppConfig.ENV + ""
				+ "<tr><td><b>Version: </b> <td>" + (version==null?"Unknown":version) + ""
				+ "</table>" +
				

		"<p></p><table class='heading' border='0' width='100%'><tr><td><p> "
				+ "<table class='lista' cellpadding='3' cellspacing='0'> "
				+ "<tr><th>product ID<th> amount <th> temp/perm <th> created<th> comment </tr>";

		int br = 0;
		boolean problematicArticles = false;
		String tempStr = "";
		
		if (rez!=null)
	  	 for (ValueObject vo : rez) {
			String trClass = (br++) % 2 == 0 ? "parni" : "neparni";
			if (vo.getStatus()=='B') {
				trClass = "greska";
				problematicArticles = true;
				}
			 

			html += "<tr class='" + trClass + "'><td>" + vo.getValue("sifra_artikl") + "<td align='right'>" + vo.getValue("kolicina")
					+ "<td>" + tempStr + "<td>" + SDF.format(vo.getCreated()) + "<td>"
					+ vo.getValue("napomena").toString().replaceAll("\n", "<br>") + "</td></tr>";

		}
		
		if (problematicArticles)
			html+="</p><p><b>there are invalid articles in order that were not stored in database and should be taken care of. They are marked RED in this mail!";

		html += "</p></td></tr></table></BODY></HTML>";

		return html;
	}
	
	private  String generateInvoiceHtml(ValueObject invoice, List<ValueObject> articles, 
			String version, long acceptedOnServer, String attachmentType) {
		String html = "<HTML><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<link rel=\"stylesheet\" href=\"" + HOST + "/resources/css/style.css\" type=\"text/css\">"
				+ "<style type=\"text/css\">" + "	 table.round{ " + "	-moz-border-radius: 8px;"
				+ "	border-radius: 8px; " + "	}" + " table.lista tr.parni { background-color:silver; } "
				+ " table.lista tr.neparni { background-color:gray; }"
				+ " table.lista tr.greska { background-color:pink; color:red;  }"
				+ " </style> </head> <BODY > ";

		List<ValueObject> rez = articles;
		String sizeMesg = (rez == null ? "(no records)" : "" + rez.size());

		int articlesAmt = 0;
		if (rez!=null)
	  	 for (ValueObject vo : rez) {
	  		articlesAmt += vo.getIntValue("kolicina");
		 }
		 
 
		html += "<h1 align='center'>Invoice #" + String.format("%06d", invoice.getSifra()) + "</h1> " + "<p align='left'> "
				+ "<table><tr><td><b>Customer ID: </b><td>" + invoice.getValue("sifra_kupac") + ""
				+ "<tr><td><b>Operater ID: </b><td> " + invoice.getCreatedBy() + "" 
  		 
				+ "<tr><td><b>Records: </b> <td>" + sizeMesg + "" + "<tr><td><b>articles: </b> <td>" + articlesAmt + ""
				+ "<tr><td><b>Environment: </b> <td>" + AppConfig.ENV + ""
				+ "<tr><td><b>Version: </b> <td>" + (version == null ? "Unknown" : version) + ""
				+ "</table>";
		 
		html += ""
			+ "<p> Please find your latest invoice in attachment to this email as " + attachmentType + " file. In case having any questions, please respond by clicking reply to this mail message.</p>"
			+ "</BODY></HTML>";

		return html;
	}


	public boolean sendCrashReportEmail(ExceptionReportDTO report, File workingTmpDir, String version) {
		boolean res = false;

		LOG.info("Trying to send crash report email");

		if (report == null) {
			return false;
		}

		File crashReportFile = generateExceptionFile(report, workingTmpDir);

		LOG.info("Got report file: " + crashReportFile.getAbsolutePath());

		String from = config.getFrom();
		int cnt = counter.incrementAndGet();
		String subject = "crash report #" + cnt;
		String fromName = "Crash reporting system";

		String message = generateCrashReportHtml(report, cnt, version);

		Properties props = generateEmailProperties();
		String ccTo = null;
		String[] bcc = null;

		try {
			LOG.info("trysend crashReport");
			 

			SendMail sendMail = new SendMail(fromName, from, toCrashReport, ccTo, bcc, replyTo, subject, message, props, config, crashReportFile);
			LOG.info("trysend inst crashReport");
			
			sendMail.send();
			LOG.info("trysend crashReport sent");
			res = true;
		} catch (Exception e) {
			LOG.error("iznimka kod slanja maila, crash report #" + counter, e);
			return false;
		} finally {
			if (crashReportFile != null)
				crashReportFile.delete();
			props.clear();
			props = null;
		}

		return res;
	}
	
	public boolean sendExceptionEmail(Exception e, File workingTmpDir, String version, String explanationMessage) {
		boolean res = false;

		LOG.info("Trying to send exception report email. E: " + e + " Version: " + version);
 
		File crashReportFile = e == null ? null : generateExceptionStackTraceFile(e, workingTmpDir);

		String from = config.getFrom();
		String subject = "exception report #" + counter;

		String message = generateExceptionReportHtml(version, explanationMessage);

		Properties props = generateEmailProperties();
		String ccTo = null;
		String[] bcc = null;

		try {
			LOG.info("trysend exception report");
			
			SendMail sendMail = new SendMail(null, from, toCrashReport, ccTo, bcc, replyTo, subject, message, props, config, crashReportFile);
			LOG.info("trysend inst exception report");
			
			sendMail.send();
			LOG.info("trysend crashReport sent");
			res = true;
		} catch (Exception ex) {
			LOG.error("iznimka kod slanja maila, exception report #" + counter, ex);
			return false;
		} finally {
			if (crashReportFile != null)
				crashReportFile.delete();
			props.clear();
			props = null;
		}

		return res;
	}

	private static File generateExceptionStackTraceFile(Exception e, File fileDir) {
		PrintStream pStream = null;
		FileOutputStream fos = null;
		File file = null;

		try {
			 
			file = new File(
					fileDir.getAbsolutePath() + "/crash_report_" + e.hashCode() + ".txt");

			fos = new FileOutputStream(file);
			pStream = new PrintStream(fos);

			e.printStackTrace(pStream);
			 

			LOG.info(file.getName() + " - file was created successfully !!!");

		} catch (Exception ex) {
			LOG.error("Error writing error report file!!!", ex);

		} finally {

			try {
				pStream.flush();
				pStream.close();
				pStream = null;
			} catch (Exception ex) {
				LOG.error("Exception while flushing/closing  !!!", ex);
			}
		}
		return file;
	}

	private String generateCrashReportHtml(ExceptionReportDTO report, int id, String version) {
		String html = "<HTML><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<link rel=\"stylesheet\" href=\"" + HOST + "/resources/css/style.css\" type=\"text/css\">"
				+ "<style type=\"text/css\">" + "	 table.round{ " + "	-moz-border-radius: 8px;"
				+ "	border-radius: 8px; " + "	}" + " table.lista tr.parni { background-color:silver; } "
				+ " table.lista tr.neparni { background-color:gray; }" + " </style> </head> <BODY > ";

		html += "<h1 align='center'>Crash report #" + id + "</h1> " + "<p align='left'> " + "<table>"
				+ "<tr><td><b>Operater ID: </b><td> " + report.getUsername() + ""
				+ "<tr><td><b>Genereated on: </b><td> " + SDF.format(report.getCreatedLocally()) + ""
				+ "<tr><td><b>Reported on: </b><td> " + SDF.format(report.getCreated()) + ""
				+ "<tr><td><b>IMEI: </b> <td>" + report.getImei() + "" + "</table>" +

		"<p></p><table class='heading' border='0' width='100%'><tr><td><p> " +
		"<p>Version in application sent data: " + version + 
		"</p></td></tr></table></BODY></HTML>";

		return html;
	}
	
	private static String generateExceptionReportHtml(String version, String explanationMessage) {
		String html = "<HTML><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />"
				+ "<link rel=\"stylesheet\" href=\"" + HOST + "/resources/css/style.css\" type=\"text/css\">"
				+ "<style type=\"text/css\">" + "	 table.round{ " + "	-moz-border-radius: 8px;"
				+ "	border-radius: 8px; " + "	}" + " table.lista tr.parni { background-color:silver; } "
				+ " table.lista tr.neparni { background-color:gray; }" + " </style> </head> <BODY > ";

		html += "<h1 align='center'>Exception report" + "</h1> " + "<p align='left'> "+
				 

		"<p></p>Exception in system happened. Please check exception stack trace in attachment and act on it.. " +
		"<p>Version in application sent data: <b>" + version + "</b>" + 
		"<p>Message: " + explanationMessage + 
		"</p></td></tr></table></BODY></HTML>";

		return html;
	}

	private static AtomicInteger counter = new AtomicInteger(0);

	private static synchronized File generateExceptionFile(ExceptionReportDTO report, File fileDir) {

		FileWriter fileWriter = null;
		File file = null;

		try {
			int cnt = counter.incrementAndGet();
			file = new File(
					fileDir.getAbsolutePath() + "/crash_report_" + report.getUsername() + "_" + cnt + ".txt");

			fileWriter = new FileWriter(file);

			fileWriter.append(report.getExceptionMessage());

			LOG.info(file.getName() + " - file was created successfully !!!");

		} catch (Exception e) {
			LOG.error("Error writing error report file!!!", e);

		} finally {

			try {
				fileWriter.flush();
				fileWriter.close();
				fileWriter = null;
			} catch (IOException e) {
				LOG.error("Error while flushing/closing fileWriter !!!", e);
			}
		}
		return file;
	}

}
