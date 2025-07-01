package com.example.carsharingonline.repository;

import com.example.carsharingonline.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"rental"})
    List<Payment> findAllByRental_User_Id(Long rentalUserId);

    Optional<Payment> findPaymentBySessionId(String sessionId);
}
