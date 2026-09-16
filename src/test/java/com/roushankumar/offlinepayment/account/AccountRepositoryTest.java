package com.roushankumar.offlinepayment.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.account.entity.Account;
import com.roushankumar.offlinepayment.account.entity.AccountStatus;
import com.roushankumar.offlinepayment.account.repository.AccountRepository;

@SpringBootTest
@Transactional
public class AccountRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  @Test
  void shouldSaveAndFindAccount() {
    Account account = new Account();
    account.setAccountNumber("DEMO-100001");
    account.setHolderName("Test User");
    account.setBalance(new BigDecimal("1000.00"));
    account.setCurrency("INR");
    account.setStatus(AccountStatus.ACTIVE);

    Account saved = accountRepository.save(account);

    assertThat(saved.getId()).isNotNull();

    Account found = accountRepository.findByAccountNumber("DEMO-100001").orElseThrow();

    assertThat(found.getHolderName()).isEqualTo("Test User");
    assertThat(found.getBalance()).isEqualByComparingTo("1000.00");
    assertThat(found.getCurrency()).isEqualTo("INR");
    assertThat(found.getStatus()).isEqualTo(AccountStatus.ACTIVE);
  }
}
