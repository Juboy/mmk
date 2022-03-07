package com.mmk.sms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mmk.sms.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	
	Optional<Account> findByUsername(String username);

}
