package com.sunce.web.dto;

import java.util.Date;

public final class ExceptionReportDTO {
 
	private String username;
	private String exceptionMessage;
	private String imei;
	private long createdLocally;
	private Date created;
	 
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public long getCreatedLocally() {
		return createdLocally;
	}

	public void setCreatedLocally(long createdLocally) {
		this.createdLocally = createdLocally;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Date getCreated() {
		
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

}
