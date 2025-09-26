package com.saga.order.repository;

import com.saga.core.model.SagaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SagaTransactionRepository extends JpaRepository<SagaTransaction, String> {
    Optional<SagaTransaction> findByOrderId(String orderId);
}

