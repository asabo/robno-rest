/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.manager;

import com.sunce.robno.rest.dto.UserCredentials;

/**
 *
 * @author ante
 */
public interface UserAuthenticator {
     public int authenticateUser(UserCredentials creds);
}
