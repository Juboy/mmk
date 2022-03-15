package com.mmk.sms.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mmk.sms.entity.Account;
import com.mmk.sms.entity.Message;
import com.mmk.sms.entity.PhoneNumber;
import com.mmk.sms.repository.AccountRepository;
import com.mmk.sms.repository.PhoneNumberRepository;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {

	@MockBean private AccountRepository accountRepository;
	@MockBean private PhoneNumberRepository phoneNumberRepository;
	@MockBean private RemoteCache<String, Message> smsCache;
	@MockBean private RemoteCache<String, Integer> requestCache;
	
	private AccountService accountService;
	
	@BeforeEach
	public void setup() {
		accountService = new AccountServiceImpl(accountRepository, phoneNumberRepository, smsCache, requestCache);
	}
	
	@Test
	public void shouldRecieveSms() {
		when(accountRepository.findByUsername(anyString())).thenReturn(
				Optional.of(Account.builder()
						.id(1)
						.username("MMK")
						.build()
				));
		when(phoneNumberRepository.findByAccountAndNumber(anyInt(), anyString())).thenReturn(
				Optional.of(PhoneNumber.builder()
						.id(1)
						.account(1)
						.number("567383920")
						.build()
						));
//		when(smsCache.putIfAbsent(anyString(), any(Message.class))).thenReturn(Message.builder().build());
		
		accountService.recieveSms("MMK", "11233", "11233", "STOP");
		Mockito.verify(smsCache).putIfAbsent(anyString(), any(Message.class), anyLong(), any(TimeUnit.class));
	}
	
	@Test
	public void shouldNotRecieveSms_to_param_not_found() {
		when(accountRepository.findByUsername(anyString())).thenReturn(
				Optional.of(Account.builder()
						.id(1)
						.username("MMK")
						.build()
				));
		when(phoneNumberRepository.findByAccountAndNumber(anyInt(), anyString())).thenReturn(
				Optional.empty());
		
		accountService.recieveSms("MMK", "11233", "11233", "STOP");
		Mockito.verify(smsCache, times(0)).putIfAbsent(anyString(), any(Message.class), anyLong(), any(TimeUnit.class));
		assertTrue(accountService.recieveSms("MMK", "11233", "11233", "STOP").getMessage().isEmpty());
	}
	
	@Test
	public void shouldSendSms() {
		when(accountRepository.findByUsername(anyString())).thenReturn(
				Optional.of(Account.builder()
						.id(1)
						.username("MMK")
						.build()
				));
		when(phoneNumberRepository.findByAccountAndNumber(anyInt(), anyString())).thenReturn(
				Optional.of(PhoneNumber.builder()
						.id(1)
						.account(1)
						.number("567383920")
						.build()
						));
		when(smsCache.containsKey(anyString()))
					.thenReturn(false);
		when(requestCache.containsKey(anyString()))
					.thenReturn(false);
		
		
		accountService.sendSms("MMK", "11233", "11233", "STOP");
		Mockito.verify(requestCache).put(anyString(), anyInt(), anyLong(), any(TimeUnit.class));
		when(requestCache.put(anyString(), anyInt(), anyLong(), any(TimeUnit.class)))
				.thenReturn(1);
		assertTrue(accountService.sendSms("MMK", "11233", "11233", "STOP").getError().isEmpty());
	}
	
	@Test
	public void shouldNotSentSMS__recepientsInStopCache() {
		when(accountRepository.findByUsername(anyString())).thenReturn(
				Optional.of(Account.builder()
						.id(1)
						.username("MMK")
						.build()
				));
		when(phoneNumberRepository.findByAccountAndNumber(anyInt(), anyString())).thenReturn(
				Optional.of(PhoneNumber.builder()
						.id(1)
						.account(1)
						.number("567383920")
						.build()
						));
		when(smsCache.containsKey(anyString()))
					.thenReturn(true);
		when(requestCache.containsKey(anyString()))
					.thenReturn(false);
		
		
		accountService.sendSms("MMK", "11233", "11233", "STOP");
		Mockito.verify(requestCache, times(0)).put(anyString(), anyInt(), anyLong(), any(TimeUnit.class));
		assertTrue(accountService.sendSms("MMK", "11233", "11233", "STOP").getMessage().isEmpty());
	}
	
	@Test
	public void shouldNotSentSMS__cacheExpiryTest() {
		when(accountRepository.findByUsername(anyString())).thenReturn(
				Optional.of(Account.builder()
						.id(1)
						.username("MMK")
						.build()
				));
		when(phoneNumberRepository.findByAccountAndNumber(anyInt(), anyString())).thenReturn(
				Optional.of(PhoneNumber.builder()
						.id(1)
						.account(1)
						.number("567383920")
						.build()
						));
		when(smsCache.containsKey(anyString()))
					.thenReturn(true);
		when(requestCache.containsKey(anyString()))
					.thenReturn(false);
		
		
		accountService.sendSms("MMK", "11233", "11233", "STOP");
		Mockito.verify(requestCache, times(0)).put(anyString(), anyInt(), anyLong(), any(TimeUnit.class));
		assertTrue(accountService.sendSms("MMK", "11233", "11233", "STOP").getMessage().isEmpty());
	}
	
	
}
