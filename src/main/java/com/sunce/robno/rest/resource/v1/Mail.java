package com.sunce.robno.rest.resource.v1;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ansa.dao.net.EmailDTO;
import com.sunce.robno.rest.manager.MailManager;

@Path("v1/mail")
@Produces(MediaType.APPLICATION_JSON)
public class Mail {

	MailManager mailManager;

    @Inject
    public Mail(MailManager mailManager) {
        this.mailManager = mailManager;
    }
  
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sendOrder")
    public boolean sendOrder(@QueryParam("id") Integer orderId) { 
    	return mailManager.sendOrderEmail(orderId);
    }
   
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sendInvoice")
    public boolean sendInvoice(EmailDTO pdf, @QueryParam("id") Integer invoiceId) {
    	 
    	return mailManager.sendInvoiceEmail(pdf, invoiceId);
    }
    
}
