package com.sunce.web.dto;

import java.util.List;

import com.ansa.dao.ValueObject;

/**
 * class used to gather all important details about order
 * @author ante
 *
 */
public class OrderCompoundDTO {

	private ValueObject order;
	private List<ValueObject> articles;
	private String username;
	private String version;
	private String operatorName;
	
	private String[] destinationEmails;
	private String ccEmail;
	
	
	public ValueObject getOrder() {
		return order;
	}
	public void setOrder(ValueObject order) {
		this.order = order;
	}
	public List<ValueObject> getArticles() {
		return articles;
	}
	public void setArticles(List<ValueObject> articles) {
		this.articles = articles;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String[] getDestinationEmails() {
		return destinationEmails;
	}
	public void setDestinationEmails(String[] destinationEmails) {
		this.destinationEmails = destinationEmails;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getCcEmail() {
		return ccEmail;
	}
	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}
	
}
