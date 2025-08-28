package com.example.carsharingonline.repository;

import com.example.carsharingonline.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Page<Rental> findRentalsByUserId(Long userId, Pageable pageable);

    Page<Rental> findRentalsByUserIdAndActualReturnDateIsNotNull(Long userId, Pageable pageable);

    Page<Rental> findRentalsByUserIdAndActualReturnDateIsNull(Long userId, Pageable pageable);

    Page<Rental> findRentalsByActualReturnDateIsNotNull(Pageable pageable);

    Page<Rental> findRentalsByActualReturnDateIsNull(Pageable pageable);

    Optional<Rental> findRentalByIdAndActualReturnDateIsNull(Long userId);

    List<Rental> findRentalsByRentalDateBeforeAndActualReturnDateIsNull(LocalDate dateOverdue);
}
