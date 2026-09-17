package com.roushankumar.offlinepayment.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(

    @NotBlank String senderAccountNumber,

    @NotBlank String receiverAccountNumber,

    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,

    @NotBlank String idempotencyKey) {

}
