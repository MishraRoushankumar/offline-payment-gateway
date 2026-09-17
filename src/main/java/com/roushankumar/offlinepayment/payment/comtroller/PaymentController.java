package com.roushankumar.offlinepayment.payment.comtroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roushankumar.offlinepayment.payment.dto.PaymentRequest;
import com.roushankumar.offlinepayment.payment.dto.PaymentResponse;
import com.roushankumar.offlinepayment.payment.entity.Payment;
import com.roushankumar.offlinepayment.payment.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @PostMapping
  public ResponseEntity<PaymentResponse> createPayment(
      @Valid @RequestBody PaymentRequest request) {
    Payment payment = paymentService.processPayment(
        request.senderAccountNumber(),
        request.receiverAccountNumber(),
        request.amount(),
        request.idempotencyKey());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(PaymentResponse.from(payment));
  }
}
