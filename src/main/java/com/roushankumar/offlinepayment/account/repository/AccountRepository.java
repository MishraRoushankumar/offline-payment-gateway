package com.roushankumar.offlinepayment.account.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roushankumar.offlinepayment.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  Optional<Account> findByAccountNumber(String accountNumber);
}
