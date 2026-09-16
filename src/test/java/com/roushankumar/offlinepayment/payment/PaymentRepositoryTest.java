package com.roushankumar.offlinepayment.payment;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.account.entity.Account;
import com.roushankumar.offlinepayment.account.entity.AccountStatus;
import com.roushankumar.offlinepayment.account.repository.AccountRepository;
import com.roushankumar.offlinepayment.payment.entity.Payment;
import com.roushankumar.offlinepayment.payment.entity.PaymentStatus;
import com.roushankumar.offlinepayment.payment.repository.PaymentRepository;

@SpringBootTest
@Transactional
class PaymentRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PaymentRepository paymentRepository;

  @Test
  void shouldSaveAndFindPayment() {
    Account sender = new Account();
    sender.setAccountNumber("DEMO-100001");
    sender.setHolderName("Sender User");
    sender.setBalance(new BigDecimal("1000.00"));
    sender.setCurrency("INR");
    sender.setStatus(AccountStatus.ACTIVE);

    Account receiver = new Account();
    receiver.setAccountNumber("DEMO-100002");
    receiver.setHolderName("Receiver User");
    receiver.setBalance(new BigDecimal("500.00"));
    receiver.setCurrency("INR");
    receiver.setStatus(AccountStatus.ACTIVE);

    accountRepository.save(sender);
    accountRepository.save(receiver);

    Payment payment = new Payment();
    payment.setTransactionId("txn_test_100001");
    payment.setSenderAccount(sender);
    payment.setReceiverAccount(receiver);
    payment.setAmount(new BigDecimal("250.00"));
    payment.setCurrency("INR");
    payment.setStatus(PaymentStatus.PENDING);
    payment.setIdempotencyKey("idem_test_100001");

    Payment saved = paymentRepository.save(payment);

    assertThat(saved.getId()).isNotNull();

    Payment found = paymentRepository
        .findByTransactionId("txn_test_100001")
        .orElseThrow();

    assertThat(found.getAmount()).isEqualByComparingTo("250.00");
    assertThat(found.getStatus()).isEqualTo(PaymentStatus.PENDING);
    assertThat(found.getCurrency()).isEqualTo("INR");

    assertThat(found.getSenderAccount().getAccountNumber())
        .isEqualTo("DEMO-100001");

    assertThat(found.getReceiverAccount().getAccountNumber())
        .isEqualTo("DEMO-100002");

  }
}