/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.manager;

import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Rezultat;

/**
 *
 * @author ante
 */
public interface StockManager {
     
	public Rezultat getStockAvailbility(Integer articleId, Integer unitId);
}
