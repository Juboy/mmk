package com.mmk.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mmk.sms.entity.PhoneNumber;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Integer>{
	Optional<PhoneNumber> findByAccountAndNumber(Integer account, String number);
}
