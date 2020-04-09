package com.sunce.robno.rest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;

import com.google.common.collect.ImmutableSet;

public class AuthFilter implements javax.servlet.Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	static Set<String> passThroughs = ImmutableSet.of("/v1/auth", "/v1/daoObject/columns", "/v1/daoObject/importedKeys",
			"/v1/daoObject/primaryKeys");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String pathInfo = req.getPathInfo();

		if (passThroughs.contains(pathInfo)) {
			chain.doFilter(request, response);
			return;
		}

		// postavke ce se uvijek moci procitati... zasada tako
		String objName = request.getParameter("name");
		if (pathInfo.equalsIgnoreCase("/v1/daoObject/") && objName != null && objName.equals("postavke")) {
			chain.doFilter(request, response);
			return;
		}

		String reqAuth = req.getHeader("X-robno-auth");

		try {
			UUID uuid = reqAuth == null ? null : UUID.fromString(reqAuth);

			Integer loggedUserIdForUUID = RobnoRestApp.getLoggedUserIdForUUID(uuid);
			if (loggedUserIdForUUID == null) 
				throw new NotAuthorizedException(response);
			
			request.setAttribute("userId", uuid == null ? null : loggedUserIdForUUID);

		} catch (IllegalArgumentException illa) {
			System.out.println("Illegal arg exception, reqAuth: " + reqAuth);
		} finally {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
