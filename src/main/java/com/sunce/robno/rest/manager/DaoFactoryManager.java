/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.manager;

import com.ansa.dao.net.Rezultat;
import com.ansa.dao.net.Upit;

/**
 *
 * @author ante
 */
public interface DaoFactoryManager {
     
     public Rezultat performQuery(Upit query);
     public int performUpdate(Upit query);
     public boolean isTableEmpty(String tableName);
     public int nextAvailableKey(String tableName, String key);
     
}
