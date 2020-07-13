package com.sunce.robno.rest.manager;

import javax.inject.Inject;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.ansa.dao.DAOFactory;
import com.ansa.dao.net.Rezultat;
import com.ansa.dao.net.Upit;
import com.ansa.util.HtmlPrintParser;


public final class StockManagerImpl implements StockManager {
	private final static Logger LOG = Log.getLogger(StockManager.class);
	
  static String predlozak = "com/sunce/robno/rest/manager/sql/stanje_skladista.sql";
  static String upitStanjeSkladista;

  static {
  		HtmlPrintParser hp = new HtmlPrintParser();
  		
  		upitStanjeSkladista = hp.ucitajHtmlPredlozak(predlozak);
  }

  private com.ansa.dao.DAOFactory daoFactory; 
    
    @Inject
    public StockManagerImpl(DAOFactory daoFactory) {
    	this.daoFactory = daoFactory;
    }

	@Override
	public synchronized Rezultat getStockAvailbility(Integer articleId, Integer unitId) {
		Upit upitVo = new Upit();
		String upitStr = upitStanjeSkladista; 
		upitStr = upitStr.replaceFirst("\\{sif_art\\}", "" + articleId);
		upitStr = upitStr.replaceFirst("\\{sif_jmj\\}", "" + unitId);
		upitVo.setUpit(upitStr);
		Rezultat rez = null;
		 
		try {
			rez = DAOFactory.performQuery(upitVo);
		} catch (Exception e) {
			LOG.warn("Iznimka kod getStockAvailbility, artId: " + articleId + " uintId: " + unitId, e);

		} finally {
			upitVo = null;
		}
		return rez;
	}
    
}
