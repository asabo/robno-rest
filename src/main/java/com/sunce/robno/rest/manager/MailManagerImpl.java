package com.sunce.robno.rest.manager;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.DAOObjekt;
import com.ansa.dao.SearchCriteriaObject;
import com.ansa.dao.ValueObject;
import com.ansa.dao.net.EmailDTO;
import com.sunce.robno.rest.RobnoRestApp;
import com.sunce.web.mail.OrderMailer;

/**
 * vraca podatke o nekom dao objektu
 */
public class MailManagerImpl implements MailManager {
	Logger LOG = LogManager.getLogger();
  
	OrderMailer orderMailer;
    
    @Inject
    public MailManagerImpl() {
    	 RobnoRestApp.getDAOFactory(); // da factory postane opreational
    	 this.orderMailer = new OrderMailer();
    }

	@Override
	public boolean sendOrderEmail(Integer orderId) {
		DAOObjekt daoObjekt = DAOFactory.getDAOObjekt("narudzba");
		return false;
	}

	@Override
	public boolean sendExceptionEmail() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendInvoiceEmail(EmailDTO invoiceDto, Integer invoiceId) {
		long start = System.currentTimeMillis();
		
		ValueObject invoicePdf;
		byte[] pdf =  invoiceDto.getAttachment();
		invoicePdf  = invoiceDto.getMeta();
		 
		System.out.println("dosao invoice za slanje, byteova: " + pdf.length); 
		
		final DAOObjekt daoRacun = DAOFactory.getDAOObjekt("racun");
		DAOObjekt daoRacunStavka = DAOFactory.getDAOObjekt("racun_stavka");
		DAOObjekt daoPartner = DAOFactory.getDAOObjekt("katalog.partner");
		
		try {
			ValueObject racun = daoRacun.read(invoiceId);
			SearchCriteriaObject sco = SearchCriteriaObject.getInstance("sifra_racun",
					Integer.valueOf(invoiceId), java.sql.Types.INTEGER, SearchCriteriaObject.CRITERIA_WORD_STRICT);
			List<ValueObject> stavke = daoRacunStavka.findAll(sco);
			ValueObject kupac = daoPartner.read(racun.getValue("sifra_kupac"));
			long acceptedOnServer = System.currentTimeMillis();
			boolean forced = false;
			
			 String email = (String) invoicePdf.getValue("to");
			
			System.out.println("dosao invoice za slanje, byteova: " + pdf.length + " racun id: " + invoiceId + " kupac email: " + email);
			
			orderMailer.sendInvoiceEmail(invoiceDto, racun, stavke, kupac, acceptedOnServer, forced);
			
			SwingUtilities.invokeLater( () -> {			
			try {
				racun.setValue("_email_racuna_poslan", Long.valueOf(Calendar.getInstance().getTime().getTime()));
				daoRacun.update(racun);
				System.out.println("racun updatean sa datumom slanja... ");
			} catch (SQLException e) {
				LOG.error("Problem pri pohranjivanju cijnjenice da je za racun " + racun.getSifra() +" poslan email uspjesno", e); 
			}}
			);
			
			return true;
		} catch (SQLException e) {
			LOG.error("Problem pri dohvatu racuna pred slanje emaila", e); 
			return false;
		} finally {
			System.out.println("slanje maila zavrseno, trajalo ms: " + (System.currentTimeMillis() - start));
		}

	}
	
	 public boolean sendInvoiceEmailExcelRRP(byte[] invoiceExcel, Integer invoiceId) {
			long start = System.currentTimeMillis();
			System.out.println("dosao excel invoice za slanje, byteova: " + invoiceExcel.length); 
			
			DAOObjekt daoRacun = DAOFactory.getDAOObjekt("racun");
			DAOObjekt daoRacunStavka = DAOFactory.getDAOObjekt("racun_stavka");
			DAOObjekt daoPartner = DAOFactory.getDAOObjekt("katalog.partner");
			
			try {
				ValueObject racun = daoRacun.read(invoiceId);
				SearchCriteriaObject sco = SearchCriteriaObject.getInstance("sifra_racun",
						Integer.valueOf(invoiceId), java.sql.Types.INTEGER, SearchCriteriaObject.CRITERIA_WORD_STRICT);
				List<ValueObject> stavke = daoRacunStavka.findAll(sco);
				ValueObject kupac = daoPartner.read(racun.getValue("sifra_kupac"));
				long acceptedOnServer = System.currentTimeMillis();
				boolean forced = false;
				
				 String email = (String) kupac.getValue("EMAIL");
				
				System.out.println("dosao excel invoice za slanje, byteova: " + invoiceExcel.length + " racun id: " + invoiceId + " kupac email: " + email);
				
				orderMailer.sendInvoiceEmailWithRRPPricesInExcel(
						invoiceExcel, racun, stavke, kupac, acceptedOnServer, forced);
				
				SwingUtilities.invokeLater(() -> {				
				try {
					racun.setValue("_email_excel_poslan", Long.valueOf(Calendar.getInstance().getTime().getTime()));
					daoRacun.update(racun);
					System.out.println("racun updatean sa datumom slanja excela... ");
				} catch (SQLException e) {
					LOG.error("Problem pri pohranjivanju cijnjenice da je za racun " + racun.getSifra() +" excel poslan email uspjesno", e); 
				} 
				});
				
				return true;
			} catch (SQLException e) {
				LOG.error("Problem pri dohvatu racuna pred slanje emaila", e); 
				return false;
			} finally {
				System.out.println("slanje excel maila zavrseno, trajalo ms: " + (System.currentTimeMillis() - start));
			}
	 }

	 
}
