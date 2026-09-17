package com.roushankumar.offlinepayment.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
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
import com.roushankumar.offlinepayment.payment.service.PaymentService;

@SpringBootTest
@Transactional
public class PaymentServiceTest {

        @Autowired
        private PaymentService paymentService;

        @Autowired
        private AccountRepository accountRepository;

        @Autowired
        private PaymentRepository paymentRepository;

        private Account sender;
        private Account receiver;

        @BeforeEach
        void setUp() {
                sender = new Account();
                sender.setAccountNumber("SERVICE-100001");
                sender.setHolderName("Sender");
                sender.setBalance(new BigDecimal("1000.00"));
                sender.setCurrency("INR");
                sender.setStatus(AccountStatus.ACTIVE);

                receiver = new Account();
                receiver.setAccountNumber("SERVICE-100002");
                receiver.setHolderName("Receiver");
                receiver.setBalance(new BigDecimal("500.00"));
                receiver.setCurrency("INR");
                receiver.setStatus(AccountStatus.ACTIVE);

                accountRepository.save(sender);
                accountRepository.save(receiver);
        }

        @Test
        void shouldProcessPaymentSuccessfully() {
                Payment payment = paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                "idem-service-100001");

                assertThat(payment.getId()).isNotNull();
                assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

                Account updatedSender = accountRepository
                                .findByAccountNumber(sender.getAccountNumber())
                                .orElseThrow();

                Account updatedReceiver = accountRepository
                                .findByAccountNumber(receiver.getAccountNumber())
                                .orElseThrow();

                assertThat(updatedSender.getBalance())
                                .isEqualByComparingTo("750.00");

                assertThat(updatedReceiver.getBalance())
                                .isEqualByComparingTo("750.00");

                assertThat(paymentRepository.findById(payment.getId()))
                                .isPresent();
        }

        @Test
        void shouldRejectPaymentWhenBalanceIsInsufficient() {
                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("1500.00"),
                                "idem-service-100002"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Insufficient balance");

                assertThat(sender.getBalance())
                                .isEqualByComparingTo("1000.00");

                assertThat(receiver.getBalance())
                                .isEqualByComparingTo("500.00");
        }

        @Test
        void shouldRejectPaymentFromBlockedAccount() {
                sender.setStatus(AccountStatus.BLOCKED);
                accountRepository.save(sender);

                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("100.00"),
                                "idem-service-100003"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Sender account is not active");
        }

        @Test
        void shouldRejectInvalidAmount() {
                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                BigDecimal.ZERO,
                                "idem-service-100004"))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Amount must be greater than zero");
        }

        @Test
        void shouldReturnExistingPaymentForSameIdempotencyKey() {
                String idempotencyKey = "idem-service-retry-001";

                Payment firstPayment = paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                idempotencyKey);

                Payment secondPayment = paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                idempotencyKey);

                assertThat(secondPayment.getId())
                                .isEqualTo(firstPayment.getId());

                assertThat(secondPayment.getTransactionId())
                                .isEqualTo(firstPayment.getTransactionId());

                Account updatedSender = accountRepository
                                .findByAccountNumber(sender.getAccountNumber())
                                .orElseThrow();

                Account updatedReceiver = accountRepository
                                .findByAccountNumber(receiver.getAccountNumber())
                                .orElseThrow();

                assertThat(updatedSender.getBalance())
                                .isEqualByComparingTo("750.00");

                assertThat(updatedReceiver.getBalance())
                                .isEqualByComparingTo("750.00");
        }

        @Test
        void shouldRejectNullIdempotencyKey() {
                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("100.00"),
                                null))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Idempotency key must not be blank");
        }

        @Test
        void shouldRejectBlankIdempotencyKey() {
                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("100.00"),
                                "   "))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Idempotency key must not be blank");
        }

        @Test
        void shouldRejectSameIdempotencyKeyWithDifferentAmount() {
                String idempotencyKey = "idem-service-conflict-001";

                paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                idempotencyKey);

                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("500.00"),
                                idempotencyKey))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage(
                                                "Idempotency key was already used with different payment details");
        }

        @Test
        void shouldRejectSameIdempotencyKeyWithDifferentReceiver() {
                String idempotencyKey = "idem-service-conflict-002";

                paymentService.processPayment(
                                sender.getAccountNumber(),
                                receiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                idempotencyKey);

                Account anotherReceiver = new Account();
                anotherReceiver.setAccountNumber("SERVICE-100003");
                anotherReceiver.setHolderName("Another Receiver");
                anotherReceiver.setBalance(new BigDecimal("100.00"));
                anotherReceiver.setCurrency("INR");
                anotherReceiver.setStatus(AccountStatus.ACTIVE);

                accountRepository.save(anotherReceiver);

                assertThatThrownBy(() -> paymentService.processPayment(
                                sender.getAccountNumber(),
                                anotherReceiver.getAccountNumber(),
                                new BigDecimal("250.00"),
                                idempotencyKey))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage(
                                                "Idempotency key was already used with different payment details");
        }

}