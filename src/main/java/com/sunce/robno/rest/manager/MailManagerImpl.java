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
import com.sunce.web.mail.OrderMailer;

/**
 * vraca podatke o nekom dao objektu
 */
public class MailManagerImpl implements MailManager {
	Logger LOG = LogManager.getLogger();
	
	DAOFactory daoFactory;
  
	OrderMailer orderMailer;
    
    @Inject
    public MailManagerImpl(DAOFactory daoFactory) {
    	 this.daoFactory = daoFactory; 
    	 this.orderMailer = new OrderMailer();
    }

	@Override
	public boolean sendOrderEmail(Integer orderId) {
		//DAOObjekt daoObjekt = DAOFactory.getDAOObjekt("narudzba");
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
		
		//byte[] attachment =  invoiceDto.getAttachment();
		 
		String emailType = invoiceDto.getEmailType();
		
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
	 
			boolean rezultat = false; 
			 	
			if (emailType.equals("PDF"))
				rezultat = orderMailer.sendInvoiceEmail(invoiceDto, racun, stavke, kupac, acceptedOnServer, forced);
			 else if (emailType.equals("Excel"))
				 rezultat =orderMailer.sendInvoiceEmailWithRRPPricesInExcel(invoiceDto,
					 racun, stavke, kupac, acceptedOnServer, forced);
			
			if (rezultat)
 			 SwingUtilities.invokeLater( () -> {		
			  try {
				String kljuc = emailType.equals("PDF") ?  "_email_racuna_poslan" : "_email_excel_poslan";
				racun.setValue(kljuc, Long.valueOf(Calendar.getInstance().getTime().getTime()));
				daoRacun.update(racun);
				System.out.println( emailType + " racun updatean sa datumom slanja... ");
			  } catch (SQLException e) {
				LOG.error("Problem pri pohranjivanju cijnjenice da je za " + emailType + " racun " + racun.getSifra() +" poslan  " + emailType + " email uspjesno", e); 
			  }}
			 );
			
			return true;
		} catch (SQLException e) {
			LOG.error("Problem pri dohvatu racuna pred slanje emaila", e); 
			return false;
		} finally {
			System.out.println("slanje " + emailType + " maila zavrseno, trajalo ms: " + (System.currentTimeMillis() - start));
			//attachment = null; //bolje sto prije rijesiti se 'teskasa' iz memorije
		}

	}
	 
}
