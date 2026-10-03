package com.roushankumar.offlinepayment.offline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;
import com.roushankumar.offlinepayment.offline.repository.OfflineTransactionRepository;
import com.roushankumar.offlinepayment.offline.service.OfflineTransactionService;

@SpringBootTest
@Transactional
class OfflineTransactionServiceTest {

  @Autowired
  private OfflineTransactionService offlineTransactionService;

  @Autowired
  private OfflineTransactionRepository offlineTransactionRepository;

  @Test
  void shouldCreateOfflineTransaction() {
    OfflineTransaction transaction = offlineTransactionService.createOfflineTransaction(
        "API-100001",
        "API-100002",
        new BigDecimal("250.00"));

    assertThat(transaction.getId()).isNotNull();
    assertThat(transaction.getOfflineTransactionId())
        .startsWith("off_");
    assertThat(transaction.getSenderAccountNumber())
        .isEqualTo("API-100001");
    assertThat(transaction.getReceiverAccountNumber())
        .isEqualTo("API-100002");
    assertThat(transaction.getAmount())
        .isEqualByComparingTo("250.00");
    assertThat(transaction.getCurrency())
        .isEqualTo("INR");
    assertThat(transaction.getStatus())
        .hasToString("CREATED");

    assertThat(
        offlineTransactionRepository
            .findByOfflineTransactionId(
                transaction.getOfflineTransactionId()))
        .isPresent();
  }

  @Test
  void shouldRejectBlankSenderAccount() {
    assertThatThrownBy(() -> offlineTransactionService.createOfflineTransaction(
        "",
        "API-100002",
        new BigDecimal("100.00")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sender account number must not be blank");
  }

  @Test
  void shouldRejectBlankReceiverAccount() {
    assertThatThrownBy(() -> offlineTransactionService.createOfflineTransaction(
        "API-100001",
        "",
        new BigDecimal("100.00")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Receiver account number must not be blank");
  }

  @Test
  void shouldRejectSameSenderAndReceiver() {
    assertThatThrownBy(() -> offlineTransactionService.createOfflineTransaction(
        "API-100001",
        "API-100001",
        new BigDecimal("100.00")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Sender and receiver accounts must be different");
  }

  @Test
  void shouldRejectInvalidAmount() {
    assertThatThrownBy(() -> offlineTransactionService.createOfflineTransaction(
        "API-100001",
        "API-100002",
        BigDecimal.ZERO))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Amount must be greater than zero");
  }
}