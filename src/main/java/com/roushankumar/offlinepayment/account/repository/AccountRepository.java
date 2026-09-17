package com.roushankumar.offlinepayment.account.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.roushankumar.offlinepayment.account.entity.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Account> findByAccountNumber(String accountNumber);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
         SELECT a
         FROM Account a
         WHERE a.accountNumber IN :accountNumbers
         ORDER BY a.accountNumber
      """)
  List<Account> findAllByAccountNumbersForUpdate(
      @Param("accountNumbers") Collection<String> accountNumbers);
}
