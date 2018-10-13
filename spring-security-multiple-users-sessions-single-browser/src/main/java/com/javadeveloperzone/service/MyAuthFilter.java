package com.javadeveloperzone.service;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.util.StringUtils;

public class MyAuthFilter extends RequestHeaderAuthenticationFilter {

	public MyAuthFilter() {
		super.setPrincipalRequestHeader("X-Alpha-Token");
		super.setCredentialsRequestHeader("");
		
		PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
	    provider.setPreAuthenticatedUserDetailsService(token -> {
	    	HttpSession session = (HttpSession) token.getPrincipal();
	    
	    	if (session != null) {
	    		return new User(token.getPrincipal().toString(), "", AuthorityUtils.createAuthorityList("ROLE_USER"));
	    	} else {
	    		throw new BadCredentialsException(token.getCredentials().toString());
	    	}
	    });
	    AuthenticationManager manager = new ProviderManager(Arrays.asList(provider));
		super.setAuthenticationManager(manager);
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principal = (String)super.getPreAuthenticatedPrincipal(request);
		HttpSession session = request.getSession(false);
		return session;
	}
}
