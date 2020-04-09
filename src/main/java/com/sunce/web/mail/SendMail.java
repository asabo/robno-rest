package com.sunce.web.mail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sunce.robno.rest.MailConfig;

public final class SendMail {
	private final static Logger LOG = LogManager.getLogger(SendMail.class);

	private String from;
	private String[] to;
	private String subject;
	private String htmlText;
	private Properties props;
	private byte[] attachment;
	private String attachmentName;
	private String attachmentType;

	private MailConfig config;

	private String fromName;

	private String ccTo;
	private String[] bcc;
	private String[] replyTo;

	public SendMail(String fromName, String from, String[] to, String ccTo, String[] bcc, String[] replyTo, String subject, String text,
			Properties props, MailConfig config) {
		this(fromName, from, to, ccTo, bcc, replyTo, subject, text, props, config, null, null, null);
	}
	
	public SendMail(String fromName, String from, String[] to, String ccTo, String[] bcc, String[] replyTo, String subject, String text,
			Properties props, MailConfig config, File crashReportFile) {
		//todo razbacati file u byte[]
		this(fromName, from, to, ccTo, bcc, replyTo, subject, text, props, config, null, null, null);
	}
	
	public SendMail(String fromName, String from, String[] to, String ccTo, String[] bcc, String[] replyTo, String subject, String text,
			Properties props, MailConfig config, byte[] attachment, String attachmentName, String attachmentType) {
		this.from = from;
		this.fromName = fromName;
		this.to = to;
		this.subject = subject;
		this.htmlText = text;
		this.props = props;
		this.attachment = attachment;
		this.attachmentName = attachmentName;
		this.attachmentType = attachmentType;
		this.config = config;
		this.ccTo = ccTo;
		this.bcc = bcc;
		this.replyTo = replyTo;
	}

	public boolean send() {

		final String u = props.getProperty("kor");
		final String p = props.getProperty("loza");
		
		System.out.println("Autenticiramo kor: " + u );

		Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(u, p);
			}
		});

		Message simpleMessage = new MimeMessage(mailSession);

		InternetAddress fromAddress = null;
		InternetAddress[] toAddress = null;
		
		InternetAddress ccAddress = null;
		InternetAddress[] bccAddress = null;
		
		InternetAddress[] replyToAddress = null;
	
		try {
			fromAddress = new InternetAddress(from);

			ccAddress = StringUtils.isEmpty(ccTo) ? null : new InternetAddress(ccTo);
			  
			List<InternetAddress> arl = new ArrayList<>(to.length);
			for (String t : to) {
				if (StringUtils.isNotEmpty(t)) {
					try {
					InternetAddress adr = new InternetAddress(t);
					arl.add(adr);
					} catch (AddressException adre) {
						LOG.warn("Problem checking receipient's email address: " + t);
					}
				}
			}
			
			toAddress = new InternetAddress[arl.size()];
			arl.toArray(toAddress);
			
			arl.clear();
			
			for (String t : bcc) {
				if (StringUtils.isNotEmpty(t)) {
					try {
					InternetAddress adr = new InternetAddress(t);
					arl.add(adr);
					} catch (AddressException adre) {
						LOG.warn("Problem checking receipient's bcc email address: " + t);
					}
				}
			}
			
			bccAddress = new InternetAddress[arl.size()];
			arl.toArray(bccAddress); arl = null;
			
			List<InternetAddress> replyToList = new ArrayList<>(replyTo.length);
			for (String t : replyTo) {
				if (StringUtils.isNotEmpty(t)) {
					try {
					InternetAddress adr = new InternetAddress(t);
					replyToList.add(adr);
					} catch (AddressException adre) {
						LOG.warn("Problem checking receipient's reply-to email address: " + t);
					}
				}
			}
			
			replyToAddress = new InternetAddress[replyToList.size()];
			replyToList.toArray(replyToAddress); replyToList = null; 
			
		} catch (AddressException e) {
			throw new RuntimeException(e);
		}

		if (attachment != null) {
			System.out.println("Saljemo attachment u mailu... ");
			return this.sendWithAttachment(attachment, attachmentType, attachmentName,  mailSession, fromAddress, toAddress, subject, htmlText, ccAddress, bccAddress, replyToAddress);
		}

		try {
			simpleMessage.setFrom(fromAddress);
			simpleMessage.addRecipients(RecipientType.TO, toAddress);
			
			if (replyToAddress!=null && replyToAddress.length>0) 
				simpleMessage.setReplyTo(replyToAddress);

			if (!StringUtils.isEmpty(ccTo))
				simpleMessage.addRecipient(Message.RecipientType.CC, ccAddress);

			if (bccAddress!=null && bccAddress.length>0) 
				simpleMessage.addRecipients(Message.RecipientType.BCC, bccAddress);

			simpleMessage.setSubject(subject);
			// simpleMessage.setText(text);
			simpleMessage.setContent(htmlText, "text/html");

			Transport transport = mailSession.getTransport(this.config.getProtocol());
			transport.connect(this.config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
			transport.sendMessage(simpleMessage, simpleMessage.getAllRecipients());
			transport.close();
			LOG.info("Email with subject: " + subject + " sent to: " + toAddress);
			return true;
		} catch (MessagingException e) {
			LOG.error("Problem while sending email with text: " + htmlText, e);
			return false;
		} catch (Exception e) {
			LOG.error("Neocekivana iznimka.", e);
			return false;
		}
	}

	private boolean sendWithAttachment(byte[] attachment, String attachmentType, String attachmentName, Session session, InternetAddress fromAddress,
			InternetAddress[] toAddress, String subject, String htmlText, InternetAddress ccAddress, InternetAddress[] bccAddress,
			InternetAddress[] replyToAddress) {

		try {

			Message message = new MimeMessage(session);
			message.setFrom(fromAddress);

			message.addRecipients(Message.RecipientType.TO, toAddress);

			if (ccAddress != null) {
				message.addRecipient(Message.RecipientType.CC, ccAddress);
			}
			
			if (replyToAddress != null && replyToAddress.length>0) {
				message.setReplyTo(replyToAddress);
			}
			
			if (bccAddress!=null && bccAddress.length>0) 
			  message.addRecipients(Message.RecipientType.BCC, bccAddress);

			message.setSubject(subject);
			message.setText("This mail contains message in HTML format and file attachment with details. If you see htis message you should consider changing email agent.");

			Multipart multipart = new MimeMultipart();

			// create file parts
			//MimeBodyPart filePartCsv = new MimeBodyPart();
			MimeBodyPart filePartTxt = new MimeBodyPart();

			DataSource source = new ByteArrayDataSource(attachment, attachmentType);

			//filePartCsv.setDataHandler(new DataHandler(source));
			//filePartCsv.setFileName(fileName);

			filePartTxt.setDataHandler(new DataHandler(source));
			filePartTxt.setFileName(attachmentName);

			// Create the html part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlText, "text/html");

			multipart.addBodyPart(messageBodyPart);
			//multipart.addBodyPart(filePartCsv);
			multipart.addBodyPart(filePartTxt);

			message.setContent(multipart);

			LOG.info("start sending email... protocol: " + this.config.getProtocol());

			Transport transport = session.getTransport(this.config.getProtocol());
			transport.connect(this.config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			int recs = message.getAllRecipients() == null ? -1 : message.getAllRecipients().length;
			LOG.info("Message sent recipients: " + recs + " type: " + message.getContentType() + " from: " + from
					+ " fa: " + fromAddress);

			//System.out.println("Done");

			return true;
		} catch (MessagingException e) {
			LOG.error("Problem while sending multipart email with file att: " + attachmentName, e);
			return false;
		} catch (Exception e) {
			LOG.error("Unexpected exception while sending multipart email with file att: " + attachmentName, e);
			return false;
		}
	}// sendWithAttachment

	 

	private String emailsToString(String[] to2) {
		String email = "";
		for (String t : to2)
			email += t + ";";

		return email;
	}

}