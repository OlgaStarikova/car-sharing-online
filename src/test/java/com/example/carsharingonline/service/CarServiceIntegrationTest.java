package com.example.carsharingonline.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.carsharingonline.CarSharingOnlineApplication;
import com.example.carsharingonline.dto.car.CarDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.service.car.CarService;
import com.example.carsharingonline.utils.TestDataUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = CarSharingOnlineApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class CarServiceIntegrationTest {
    @Autowired
    private CarService carService;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("""
            Test deleteById method, valid result
            """)
    void deleteCar_softDelete_works() {
        // GIVEN — создаём через EntityManager
        Car car = TestDataUtil.getTestCar();
        car.setId(null);
        entityManager.persist(car);
        entityManager.flush();
        entityManager.clear();

        Long carId = car.getId();

        // Проверка: машина видна
        List<CarDto> all = carService.findAll(PageRequest.of(0, 10));
        assertThat(all).hasSize(1);

        // WHEN
        carService.deleteCar(carId);

        // THEN
        assertThrows(EntityNotFoundException.class,
                () -> carService.findById(carId));

        // findAll больше не видит
        assertThat(carService.findAll(PageRequest.of(0, 10))).isEmpty();

        // Но в БД есть с isDeleted = true
        Car deleted = entityManager.find(Car.class, carId);
        assertTrue(deleted.isDeleted());
    }
}
