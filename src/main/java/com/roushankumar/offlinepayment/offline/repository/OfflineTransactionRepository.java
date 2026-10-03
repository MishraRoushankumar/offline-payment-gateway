package com.roushankumar.offlinepayment.offline.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.roushankumar.offlinepayment.offline.entity.OfflineTransaction;

public interface OfflineTransactionRepository extends JpaRepository<OfflineTransaction, UUID> {

  Optional<OfflineTransaction> findByOfflineTransactionId(
      String offlineTransactionId);
}
