package com.mmk.sms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Builder
@Entity
@Data
@Table(name = "phone_number")
public class PhoneNumber {
	
	@Id
	private Integer id;
	
	@Column(name = "number")
	private String number;
	
	@Column(name = "account_id")
	private Integer account;
	

}
