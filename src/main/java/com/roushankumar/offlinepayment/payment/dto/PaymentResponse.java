package com.roushankumar.offlinepayment.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.roushankumar.offlinepayment.payment.entity.Payment;

public record PaymentResponse(
    UUID id,
    String transactionId,
    String senderAccountNumber,
    String receiverAccountNumber,
    BigDecimal amount,
    String currency,
    String status) {

  public static PaymentResponse from(Payment payment) {
    return new PaymentResponse(
        payment.getId(),
        payment.getTransactionId(),
        payment.getSenderAccount().getAccountNumber(),
        payment.getReceiverAccount().getAccountNumber(),
        payment.getAmount(),
        payment.getCurrency(),
        payment.getStatus().name());
  }
}
