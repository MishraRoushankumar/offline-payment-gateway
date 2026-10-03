package com.roushankumar.offlinepayment.offline;

import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;
import com.roushankumar.offlinepayment.offline.repository.OfflineTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OfflineTransactionRepositoryTest {

  @Autowired
  private OfflineTransactionRepository offlineTransactionRepository;

  @Test
  void shouldSaveAndFindOfflineTransaction() {
    OfflineTransaction transaction = new OfflineTransaction();

    transaction.setOfflineTransactionId("offline-test-001");
    transaction.setSenderAccountNumber("API-100001");
    transaction.setReceiverAccountNumber("API-100002");
    transaction.setAmount(new BigDecimal("150.00"));
    transaction.setCurrency("INR");

    OfflineTransaction saved = offlineTransactionRepository.save(transaction);

    OfflineTransaction found = offlineTransactionRepository
        .findByOfflineTransactionId("offline-test-001")
        .orElseThrow();

    assertThat(found.getId()).isEqualTo(saved.getId());
    assertThat(found.getOfflineTransactionId())
        .isEqualTo("offline-test-001");
    assertThat(found.getSenderAccountNumber())
        .isEqualTo("API-100001");
    assertThat(found.getReceiverAccountNumber())
        .isEqualTo("API-100002");
    assertThat(found.getAmount())
        .isEqualByComparingTo("150.00");
    assertThat(found.getCurrency()).isEqualTo("INR");
  }
}