package com.roushankumar.offlinepayment.offline.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;
import com.roushankumar.offlinepayment.offline.repository.OfflineTransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OfflineTransactionService {

  private final OfflineTransactionRepository offlineTransactionRepository;

  @Transactional
  public OfflineTransaction createOfflineTransaction(
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount) {
    validate(senderAccountNumber, receiverAccountNumber, amount);

    OfflineTransaction transaction = new OfflineTransaction();

    transaction.setOfflineTransactionId("off_" + UUID.randomUUID());
    transaction.setSenderAccountNumber(senderAccountNumber);
    transaction.setReceiverAccountNumber(receiverAccountNumber);
    transaction.setAmount(amount);
    transaction.setCurrency("INR");

    return offlineTransactionRepository.save(transaction);
  }

  private void validate(
      String senderAccountNumber,
      String receiverAccountNumber,
      BigDecimal amount) {
    if (senderAccountNumber == null || senderAccountNumber.isBlank()) {
      throw new IllegalArgumentException(
          "Sender account number must not be blank");
    }

    if (receiverAccountNumber == null || receiverAccountNumber.isBlank()) {
      throw new IllegalArgumentException(
          "Receiver account number must not be blank");
    }

    if (senderAccountNumber.equals(receiverAccountNumber)) {
      throw new IllegalArgumentException(
          "Sender and receiver accounts must be different");
    }

    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Amount must be greater than zero");
    }
  }
}