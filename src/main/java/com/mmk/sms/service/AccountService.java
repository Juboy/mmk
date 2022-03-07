package com.mmk.sms.service;

import com.mmk.sms.entity.Response;

public interface AccountService {

	/**
	 * 
	 * @param username 
	 * @param from 
	 * @param to Recipients number (must be in db)
	 * @param text
	 * @return
	 */
	Response recieveSms(String username, String from, String to, String text);
	
	Response sendSms(String username, String from, String to, String text);
}
