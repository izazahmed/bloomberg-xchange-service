package com.slb.apigee.xchange.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.httpBasic().disable()
		.csrf().disable() //--- with out this code, we are not able to do POST/PUT/DELETE ops, only GET
		//.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER) 
		.headers(headers -> headers.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'")));
		//.headers(headers -> headers.httpStrictTransportSecurity().disable());
	}
}
