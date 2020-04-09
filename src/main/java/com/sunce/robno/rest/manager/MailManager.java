/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.manager;

import com.ansa.dao.net.EmailDTO;

/**
 *
 * @author ante
 */
public interface MailManager {
     
     public boolean sendOrderEmail(Integer orderId);
     public boolean sendExceptionEmail();
     public boolean sendInvoiceEmail(EmailDTO invoicePdf, Integer invoiceId);
     public boolean sendInvoiceEmailExcelRRP(byte[] invoiceExcel, Integer invoiceId);

}
