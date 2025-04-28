package com.example.carsharingonline.repository;

import com.example.carsharingonline.model.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findCarByIdAndAndInventoryEquals(Long carId,int inventoryValue);
}
