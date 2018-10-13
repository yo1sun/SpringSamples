package com.javadeveloperzone.service;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@Service
public class LoginService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	private SessionAuthenticationStrategy sessionStrategy = new MySessionAuthenticationStrategy();
	private AuthenticationSuccessHandler successHandler = new MyAuthenticationSuccessHandler();
	private AuthenticationFailureHandler failureHandler = new MyAuthenticationFailureHandler();

	public LoginService() {
	}
	
	public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Authentication authResult;

		try {
			// Authentication
			authResult = attemptAuthentication(request, response);
			if (authResult == null) {
				return;
			}
			
			// Create New Session
			sessionStrategy.onAuthentication(authResult, request, response);			
		}
		catch (InternalAuthenticationServiceException failed) {
			// Authentication failed
			unsuccessfulAuthentication(request, response, failed);
			return;
		}
		catch (AuthenticationException failed) {
			// Authentication failed
			unsuccessfulAuthentication(request, response, failed);
			return;
		}

		// Authentication success
		successfulAuthentication(request, response, authResult);
	}

	private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		if (username == null) {
			username = "";
		}

		if (password == null) {
			password = "";
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		// Allow subclasses to set the "details" property
		authRequest.setDetails(new WebAuthenticationDetails(request));

		return authenticationManager.authenticate(authRequest);
	}

	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter("password");
	}

	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter("username");
	}
	
	
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
			throws IOException, ServletException {

		// Save Auth to Session
		SecurityContextHolder.getContext().setAuthentication(authResult);

		//
		successHandler.onAuthenticationSuccess(request, response, authResult);
	}

	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
			throws IOException, ServletException {
		
		// Clear Auth from Session
		SecurityContextHolder.clearContext();

		// 
		failureHandler.onAuthenticationFailure(request, response, failed);
	}
	
	/**
	 * 
	 * @author youichisatoh
	 *
	 */
	public static class MySessionAuthenticationStrategy extends SessionFixationProtectionStrategy {
		
		public MySessionAuthenticationStrategy() {
			super.setMigrateSessionAttributes(false);
		}

		@Override
		protected void onSessionChange(String originalSessionId, HttpSession newSession, Authentication auth) {
			
			String newId = newSession.getId();
			
			super.onSessionChange( originalSessionId,  newSession,  auth);
		}
	}
	
	/**
	 * 
	 * @author youichisatoh
	 *
	 */
	public static class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
		
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
				throws ServletException, IOException {
			//
		}
	}
	
	/**
	 * 
	 * @author youichisatoh
	 *
	 */
	public static class MyAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
		
		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
				throws IOException, ServletException {

				logger.debug("No failure URL set, sending 401 Unauthorized error");

				response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"Authentication Failed: " + exception.getMessage());
		}
	}
	

}
