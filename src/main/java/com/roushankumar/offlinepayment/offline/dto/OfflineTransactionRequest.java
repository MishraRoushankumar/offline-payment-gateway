package com.roushankumar.offlinepayment.offline.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OfflineTransactionRequest(
    @NotBlank String senderAccountNumber,
    @NotBlank String receiverAccountNumber,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount) {

}
