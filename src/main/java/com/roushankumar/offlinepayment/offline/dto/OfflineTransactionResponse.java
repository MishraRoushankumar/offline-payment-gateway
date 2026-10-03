package com.roushankumar.offlinepayment.offline.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;

public record OfflineTransactionResponse(
    UUID id,
    String offlineTransactionId,
    String senderAccountNumber,
    String receiverAccountNumber,
    BigDecimal amount,
    String currency,
    String status,
    OffsetDateTime createdAt) {

  public static OfflineTransactionResponse from(OfflineTransaction transaction) {
    return new OfflineTransactionResponse(
        transaction.getId(),
        transaction.getOfflineTransactionId(),
        transaction.getSenderAccountNumber(),
        transaction.getReceiverAccountNumber(),
        transaction.getAmount(),
        transaction.getCurrency(),
        transaction.getStatus().name(),
        transaction.getCreatedAt());
  }
}
