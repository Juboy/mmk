package com.mmk.sms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Response {
	
	@Default
	private String message = "";
	@Default
	private String error = "";

}
