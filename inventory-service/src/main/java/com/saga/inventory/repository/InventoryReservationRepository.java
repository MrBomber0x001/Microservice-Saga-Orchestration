package com.saga.inventory.repository;

import com.saga.inventory.model.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    Optional<InventoryReservation> findByOrderId(String orderId);
}

