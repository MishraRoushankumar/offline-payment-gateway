package com.roushankumar.offlinepayment.payment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roushankumar.offlinepayment.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

  Optional<Payment> findByTransactionId(String transactionId);

  Optional<Payment> findByIdempotencyKey(String idempotencyKey);

}
