package com.roushankumar.offlinepayment.offline.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor()
@Entity
@Table(name = "offline_transactions")
public class OfflineTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "offline_transaction_id", nullable = false, unique = true, length = 64)
  private String offlineTransactionId;

  @Column(name = "sender_account_number", nullable = false, length = 32)
  private String senderAccountNumber;

  @Column(name = "receiver_account_number", nullable = false, length = 32)
  private String receiverAccountNumber;

  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency = "INR";

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OfflineTransactionStatus status = OfflineTransactionStatus.CREATED;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "submitted_at")
  private OffsetDateTime submittedAt;

  @Column(name = "reconciled_at")
  private OffsetDateTime reconciledAt;

  @PrePersist
  protected void onCreate() {
    OffsetDateTime now = OffsetDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = OffsetDateTime.now();
  }

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;
}