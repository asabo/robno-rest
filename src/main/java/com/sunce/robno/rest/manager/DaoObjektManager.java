/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.manager;

import java.util.List;
import java.util.Set;

import com.ansa.dao.ValueObject;
import com.ansa.dao.net.Kolona;
import com.ansa.dao.net.Rezultat;

/**
 *
 * @author ante
 */
public interface DaoObjektManager {
     public Set<Kolona> getTableColumns(String name);
     public List<Kolona> getTablePrimaryKey(String name);
     public List<Kolona> getTableImportedKeys(String name);
     
     public ValueObject readObject(String name, String id);
     public Rezultat readAll(String name, Object key);
     public int insertObject(String name, ValueObject object);
     public boolean updateObject(String name, ValueObject object);
     public boolean deleteObject(String name, int id);
     
}
