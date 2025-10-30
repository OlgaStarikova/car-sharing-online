package com.example.carsharingonline.repository;

import com.example.carsharingonline.model.Car;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByIdAndIsDeletedFalse(Long id);

    Page<Car> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Car> findCarByIdAndAndInventoryEquals(Long carId,int inventoryValue);
}
