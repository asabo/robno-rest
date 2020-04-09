package com.sunce.robno.rest;

public class MailConfig {
	
	private String host;
	private String from;
	private String username;
	private String password; 
	private String protocol;
	private boolean startTLS;
	private int port;
	
	private String orderReceivers;
	private String crashReportReceivers;
	private String replyTo;
	
	private boolean auth;
	private boolean testMode;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getOrderReceivers() {
		return orderReceivers;
	}

	public void setOrderReceivers(String orderReceivers) {
		this.orderReceivers = orderReceivers;
	}

	public String getCrashReportReceivers() {
		return crashReportReceivers;
	}

	public void setCrashReportReceivers(String crashReportReceivers) {
		this.crashReportReceivers = crashReportReceivers;
	}

	public boolean isAuth() {
		return auth;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean isStartTLS() {
		return startTLS;
	}

	public void setStartTLS(boolean startTLS) {
		this.startTLS = startTLS;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
}
