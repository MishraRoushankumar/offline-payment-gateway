package com.roushankumar.offlinepayment.payment.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.account.entity.Account;
import com.roushankumar.offlinepayment.account.entity.AccountStatus;
import com.roushankumar.offlinepayment.account.repository.AccountRepository;
import com.roushankumar.offlinepayment.payment.entity.Payment;
import com.roushankumar.offlinepayment.payment.entity.PaymentStatus;
import com.roushankumar.offlinepayment.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final AccountRepository accountRepository;
  private final PaymentRepository paymentRepository;

  @Transactional
  public Payment processPayment(
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount,
      String idempotencyKey) {

    validateAmount(amount);
    validateIdempotencyKey(idempotencyKey);

    Payment existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey).orElse(null);

    if (existingPayment != null) {
      validateIdempotentRetry(
          existingPayment,
          senderAccountNumber,
          receiverAccountNumber,
          amount);

      return existingPayment;
    }

    if (senderAccountNumber.equals(receiverAccountNumber)) {
      throw new IllegalArgumentException("Sender and receiver accounts must be different");
    }

    List<Account> accounts = accountRepository.findAllByAccountNumbersForUpdate(
        List.of(senderAccountNumber, receiverAccountNumber));

    if (accounts.size() != 2) {
      throw new IllegalArgumentException("Sender or receiver account not found");
    }

    Account sender = accounts.stream()
        .filter(account -> account.getAccountNumber().equals(senderAccountNumber))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

    Account receiver = accounts.stream()
        .filter(account -> account.getAccountNumber().equals(receiverAccountNumber))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

    validateAccount(sender, "Sender");
    validateAccount(receiver, "Receiver");

    if (sender.getBalance().compareTo(amount) < 0) {
      throw new IllegalArgumentException("Insufficient balance");
    }

    sender.setBalance(sender.getBalance().subtract(amount));
    receiver.setBalance(receiver.getBalance().add(amount));

    Payment payment = new Payment();
    payment.setTransactionId("txn_" + UUID.randomUUID());
    payment.setSenderAccount(sender);
    payment.setReceiverAccount(receiver);
    payment.setAmount(amount);
    payment.setCurrency(sender.getCurrency());
    payment.setStatus(PaymentStatus.COMPLETED);
    payment.setIdempotencyKey(idempotencyKey);

    return paymentRepository.save(payment);
  }

  private void validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
  }

  private void validateAccount(Account account, String accountType) {
    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new IllegalArgumentException(accountType + " account is not active");
    }
  }

  private void validateIdempotencyKey(String idempotencyKey) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new IllegalArgumentException("Idempotency key must not be blank");
    }
  }

  private void validateIdempotentRetry(
      Payment existingPayment,
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount) {
    boolean sameSender = existingPayment.getSenderAccount()
        .getAccountNumber()
        .equals(senderAccountNumber);

    boolean sameReceiver = existingPayment.getReceiverAccount()
        .getAccountNumber()
        .equals(receiverAccountNumber);

    boolean sameAmount = existingPayment.getAmount()
        .compareTo(amount) == 0;

    if (!sameSender || !sameReceiver || !sameAmount) {
      throw new IllegalArgumentException(
          "Idempotency key was already used with different payment details");
    }
  }
}
