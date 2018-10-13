package com.javadeveloperzone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.javadeveloperzone.service.MyAuthFilter;


/**
 * Created by Java Developer Zone on 15-11-2017.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired      // here is configuration related to spring boot basic authentication
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()                                               // static users
            .withUser("zone1").password("mypassword").roles("USER")
            .and()
            .withUser("zone2").password("mypassword").roles("USER")
            .and()
            .withUser("zone3").password("mypassword").roles("USER")
            .and()
            .withUser("zone4").password("mypassword").roles("USER")
            .and()
            .withUser("zone5").password("mypassword").roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//        	// custom filter
//        	.addFilter(myAuthFilter())
        	.sessionManagement()
        		// 同じセッションで異なるユーザーでログインしたとき継続してしまう
        		.sessionFixation().newSession()
        		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        	.and()
        	.csrf().disable()
            .authorizeRequests()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated()
            ;
    }
    
//    @Bean
//    public MyAuthFilter myAuthFilter() {
//    	return new MyAuthFilter();
//    }
}
