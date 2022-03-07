package com.mmk.sms;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mmk.sms.entity.Message;

@Configuration
@EnableCaching
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService);
			
	}
	
	@Bean("smsStopCache")
    public RemoteCache<String, Message> smsStopCache(RemoteCacheManager remoteCacheManager) {
        return remoteCacheManager
        		.administration().getOrCreateCache("smsStopCache", DefaultTemplate.LOCAL);
      
    }
	
	@Bean("requestCache")
	public RemoteCache<String, Integer> requestCache(RemoteCacheManager remoteCacheManager){
		return remoteCacheManager.administration()
				.getOrCreateCache("smsRequestCountCache", DefaultTemplate.LOCAL);
	}
}
