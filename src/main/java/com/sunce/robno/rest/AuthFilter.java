package com.sunce.robno.rest;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AuthFilter implements javax.servlet.Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		if ("/v1/auth".equals(req.getPathInfo())) {
			chain.doFilter(request, response);
			return;
		}
		
		
		String reqAuth = req.getHeader("X-robno-auth");
		
		try {
		UUID uuid = reqAuth==null ? null : UUID.fromString(reqAuth);
			
				
		request.setAttribute("userId", uuid == null ? null : RobnoRestApp.getLoggedUserIdForUUID(uuid));
		}
		catch(IllegalArgumentException illa) {
			System.out.println("Illegal arg exception, reqAuth: " + reqAuth);
		}
		finally {
		 chain.doFilter(request, response);	
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
