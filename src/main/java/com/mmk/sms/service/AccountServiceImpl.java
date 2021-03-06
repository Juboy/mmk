package com.mmk.sms.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mmk.sms.entity.Account;
import com.mmk.sms.entity.Message;
import com.mmk.sms.entity.PhoneNumber;
import com.mmk.sms.entity.Response;
import com.mmk.sms.repository.AccountRepository;
import com.mmk.sms.repository.PhoneNumberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService, UserDetailsService{
	
	// TODO: Note that most hardcoded numbers here should be parsed in as env variables in the application.properties file	
	
	private final AccountRepository accountRepository;
	private final PhoneNumberRepository phoneNumberRepository;
	private final RemoteCache<String, Message> smsStopCache;
	private final RemoteCache<String, Integer> requestCache;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> user = accountRepository.findByUsername(username);
		if(user.isPresent()) {
			return user.get();
		}
		throw new UsernameNotFoundException("Invalid User");
	}

	@Override
	public Response recieveSms(String username, String from, String to, String text) {
		Optional<Account> user = accountRepository.findByUsername(username);
		
		if(text.trim().equals("STOP")) {
			String key = String.format("%s_%s", from, to);
			log.debug(user.get().getId().toString());
			Optional<PhoneNumber> phoneNumberOptional = phoneNumberRepository.findByAccountAndNumber(user.get().getId(), to);
			if(!phoneNumberOptional.isPresent()) {
				return Response.builder().error("to parameter not found").build();
			}
			
			Message message = Message.builder()
					.from(user.get().getUsername())
					.to(to)
					.build();
			//put in cache to expire in 4 hours
			smsStopCache.putIfAbsent(key, message, 4, TimeUnit.HOURS);
			return Response.builder().message("inbound sms ok").build();
			
		}
		
		return Response.builder().error("unknown failure").build();
	}
	
	public Response sendSms(String username, String from, String to, String text) {
		Optional<Account> user = accountRepository.findByUsername(username);
		Optional<PhoneNumber> phoneNumberOptional = phoneNumberRepository.findByAccountAndNumber(user.get().getId(), from);
		if(!phoneNumberOptional.isPresent()) return Response.builder().error("from parameter not found").build();
		
		//check if any entry matches the in stop cache
		String key = String.format("%s_%s", to, from);
		if(smsStopCache.containsKey(key)) return Response.builder().error(String.format("sms from %s to %s blocked by STOP request", from, to)).build();
		//check if number of requests from is already at it's threshold
		//increment number of request from the FROM number
		if(requestCache.containsKey(from)) {
			Integer numRequests = requestCache.get(from);
			if(numRequests >= 50) return Response.builder().error("limit reached for from "+ from).build();
			
			
			long expiry = calculateLifespan(requestCache.getWithMetadata(from).getCreated(), 
					requestCache.getWithMetadata(from).getLifespan());
			
			requestCache.replace(from, requestCache.get(from) + 1, expiry, TimeUnit.SECONDS);
		}else {
			//save for 24 hours
			requestCache.put(from, 1, 24, TimeUnit.HOURS);
		}

		
		return Response.builder().message("outbound sms ok").build();
	}

	
	private long calculateLifespan(long createdDate, long timespan) {
		LocalDateTime triggerTime =
		        LocalDateTime.ofInstant(Instant.ofEpochMilli(createdDate), 
		                                TimeZone.getDefault().toZoneId());
		
		LocalDateTime expiryTime = triggerTime.plusSeconds(timespan);
		
		
		LocalDateTime now = LocalDateTime.now();
		
		log.info(expiryTime.toString());
		
		return ChronoUnit.SECONDS.between(now, expiryTime);
		
		
	}

}
