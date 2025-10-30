package com.example.carsharingonline.service;

import static com.example.carsharingonline.utils.TestDataUtil.TEST_CAR_ID_AVAILABLE;
import static com.example.carsharingonline.utils.TestDataUtil.getTestCarDtoAvailable;
import static com.example.carsharingonline.utils.TestDataUtil.getTestCreateCarRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingonline.dto.car.CarDto;
import com.example.carsharingonline.dto.car.CreateCarRequestDto;
import com.example.carsharingonline.exception.CarNotAvailableException;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.mapper.CarMapper;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.repository.CarRepository;
import com.example.carsharingonline.service.car.impl.CarServiceImpl;
import com.example.carsharingonline.utils.TestDataUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(MockitoExtension.class)
public class CarServiceUnitTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carService;
    private CreateCarRequestDto requestDto;
    private CarDto carDto;

    @BeforeEach
    void setUp() {
        requestDto = getTestCreateCarRequestDto();
        carDto = getTestCarDtoAvailable();
    }

    @AfterEach
    @Sql(statements = "DELETE FROM cars", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void cleanUp() {
    }

    @Test
    @DisplayName("""
            Test save method, valid result
            """)
    public void save_validParameters_ok() {
        CarDto expected = carDto;
        Car car = TestDataUtil.getTestCar();
        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(expected);

        CarDto actual = carService.save(requestDto);
        assertNotNull(actual);
        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("""
            Test save method when input parameters is empty, should throw exception
            """)
    public void save_emptyInputParameters_not_ok() {
        CreateCarRequestDto requestDto = null;
        String expected = "Input parameters can't be null";

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> carService.save(requestDto));

        assertTrue(thrown.getMessage().contains(expected));
    }

    @Test
    @DisplayName("""
            Test method findcarById ,valid result
            """)
    public void findCarById_validParameters_ok() {
        CarDto expected = carDto;
        Car car = TestDataUtil.getTestCar();
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto actual = carService.findById(TEST_CAR_ID_AVAILABLE);

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("""
            Test updateCarById method, valid result
            """)
    public void updateCarById_validParameters_ok() {
        Car car = TestDataUtil.getTestCar();
        when(carMapper.toModel(requestDto)).thenReturn(car);
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(carDto);

        CarDto expected = carDto;
        CarDto actual = carService.updateCar(TEST_CAR_ID_AVAILABLE, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    void updateCar_NonExistingId_not_ok() {
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> carService.updateCar(TEST_CAR_ID_AVAILABLE, requestDto),
                "Can't find car by id " + TEST_CAR_ID_AVAILABLE);
    }

    @Test
    void findAll_ValidPageable_ok() {
        Car car = TestDataUtil.getTestCar();
        Pageable pageable = PageRequest.of(0, 10);
        List<Car> cars = List.of(car);
        Page<Car> page = new PageImpl<>(cars);
        when(carRepository.findAllByIsDeletedFalse(pageable)).thenReturn(page);
        when(carMapper.toDto(any(Car.class))).thenReturn(carDto);
        List<CarDto> result = carService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(carDto, result.get(0));
        verify(carRepository).findAllByIsDeletedFalse(pageable);
        verify(carMapper).toDto(car);
    }

    @Test
    void updateCarInventory_ExistingId_ok() {
        Car car = TestDataUtil.getTestCar();
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        carService.updateCarInventory(TEST_CAR_ID_AVAILABLE, 2);

        assertEquals(3, car.getInventory());
    }

    @Test
    void updateCarInventory_NonExistingId_not_ok() {
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> carService.updateCarInventory(TEST_CAR_ID_AVAILABLE, 2),
                "Can't find car by id " + TEST_CAR_ID_AVAILABLE);
    }

    @Test
    void checkCarAvailability_AvailableCar_ok() {
        Car car = TestDataUtil.getTestCar();
        car.setInventory(1);
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.of(car));

        Car result = carService.checkCarAvailability(TEST_CAR_ID_AVAILABLE);

        assertNotNull(result);
        assertEquals(car, result);
    }

    @Test
    void checkCarAvailability_NonExistingId_not_ok() {
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> carService.checkCarAvailability(TEST_CAR_ID_AVAILABLE),
                "Can't find car by id " + TEST_CAR_ID_AVAILABLE);
    }

    @Test
    void checkCarAvailability_UnavailableCar_ThrowsCarNotAvailableException() {
        Car car = TestDataUtil.getTestCar();
        car.setInventory(0);
        when(carRepository.findByIdAndIsDeletedFalse(
                TEST_CAR_ID_AVAILABLE)).thenReturn(Optional.of(car));

        assertThrows(CarNotAvailableException.class,
                () -> carService.checkCarAvailability(TEST_CAR_ID_AVAILABLE),
                "This car is not available");
    }

}
