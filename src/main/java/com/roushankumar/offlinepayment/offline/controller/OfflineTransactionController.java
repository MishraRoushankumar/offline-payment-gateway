package com.roushankumar.offlinepayment.offline.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roushankumar.offlinepayment.offline.dto.OfflineTransactionRequest;
import com.roushankumar.offlinepayment.offline.dto.OfflineTransactionResponse;
import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;
import com.roushankumar.offlinepayment.offline.service.OfflineTransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/offline-transactions")
@RequiredArgsConstructor
public class OfflineTransactionController {

  private final OfflineTransactionService offlineTransactionService;

  @PostMapping
  public ResponseEntity<OfflineTransactionResponse> createOfflineTransaction(
      @Valid @RequestBody OfflineTransactionRequest request) {
    OfflineTransaction transaction = offlineTransactionService.createOfflineTransaction(
        request.senderAccountNumber(),
        request.receiverAccountNumber(),
        request.amount());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(OfflineTransactionResponse.from(transaction));
  }
}