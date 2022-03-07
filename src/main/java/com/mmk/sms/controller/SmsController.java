package com.mmk.sms.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mmk.sms.entity.Response;
import com.mmk.sms.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(name = "/")
public class SmsController {
	
	private final AccountService accountService;
	
	@GetMapping
	public String ping() {
		return "Success";
	}
	
	@PostMapping("/inbound/sms")
	public Response InboundSms(Principal loggedInUser, @RequestParam String from, @RequestParam String to, @RequestParam String text){
		
		return accountService.recieveSms(loggedInUser.getName(), from, to, text);
		
	}
	
	@PostMapping("/outbound/sms")
	public Response outBoundSms(Principal loggedInUser, @RequestParam String from, @RequestParam String to, @RequestParam String text){
		
		return accountService.sendSms(loggedInUser.getName(), from, to, text);
		
		
	}

}
