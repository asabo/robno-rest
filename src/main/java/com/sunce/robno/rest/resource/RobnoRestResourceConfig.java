/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunce.robno.rest.resource;

import javax.ws.rs.BeanParam;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.sunce.robno.rest.manager.DaoObjektManager;
import com.sunce.robno.rest.manager.DaoObjektManagerImpl;
import com.sunce.robno.rest.manager.UserAuthenticator;
import com.sunce.robno.rest.manager.UserAuthenticatorImpl;

/**
 *
 * @author ante
 */
public class RobnoRestResourceConfig extends ResourceConfig {
	
    public RobnoRestResourceConfig() {

        // where the resource classes are
        packages("com.sunce.robno.rest.resource"); 
        setApplicationName("RobnoRest");

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(UserAuthenticatorImpl.class).to(UserAuthenticator.class);
                bind(DaoObjektManagerImpl.class).to(DaoObjektManager.class);
            }
        });
    }
    
    @BeanParam
    public ObjectMapper jacksonObjectMapper() {
        return new CustomObjectMapper();
    }

    @BeanParam
    public SerializationConfig serializationConfig() {
        return jacksonObjectMapper().getSerializationConfig();
    }

}

class CustomObjectMapper extends ObjectMapper {
    public CustomObjectMapper() {
        this.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS, false);
    }
}