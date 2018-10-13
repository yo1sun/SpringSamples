package com.javadeveloperzone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;


/**
 * Created by Java Developer Zone on 13-11-2017.
 */
@Configuration
@EnableSpringHttpSession
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public MapSessionRepository sessionRepository() {
    	return new MapSessionRepository();
    }
    
    @Bean
    public HeaderHttpSessionStrategy httpSessionStrategy() {
    	HeaderHttpSessionStrategy strategy = new HeaderHttpSessionStrategy();
    	strategy.setHeaderName("X-Alpha-Token");
    	return strategy;
    }
    
}