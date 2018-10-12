package com.javadeveloperzone;

import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;


/**
 * Created by Java Developer Zone on 13-11-2017.
 */
@Configuration
//@EnableRedisHttpSession
@EnableSpringHttpSession
public class HttpSessionConfig extends AbstractHttpSessionApplicationInitializer {

//    @Bean
//    public JedisConnectionFactory connectionFactory() {
//        return new JedisConnectionFactory();                // redis configuration
//    }

    @Bean
    public EmbeddedServletContainerCustomizer customizer() { // allowed space in cookies name - http://javadeveloperzone.com/common-error/java-lang-illegalargumentexception-invalid-character-32-present-cookie-value/
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
                tomcat.addContextCustomizers(context -> context.setCookieProcessor(new LegacyCookieProcessor()));
            }
        };
    }
    
    @Bean
    public CookieSerializer cookieSerializer() {
    	DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    	serializer.setCookieMaxAge(1800);
    	return serializer;
    }
    
    @Bean
    public MapSessionRepository sessionRepository() {
    	return new MapSessionRepository();
    }
}