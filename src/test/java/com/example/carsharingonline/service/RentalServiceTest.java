package com.example.carsharingonline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingonline.dto.car.CarDto;
import com.example.carsharingonline.dto.rental.CreateRentalRequestDto;
import com.example.carsharingonline.dto.rental.RentalDto;
import com.example.carsharingonline.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingonline.exception.CarNotAvailableException;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.mapper.RentalMapper;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.RentalRepository;
import com.example.carsharingonline.security.AuthenticationService;
import com.example.carsharingonline.service.car.CarService;
import com.example.carsharingonline.service.notification.telegram.TelegramNotificationService;
import com.example.carsharingonline.service.rental.impl.RentalServiceImpl;
import com.example.carsharingonline.service.user.UserService;
import com.example.carsharingonline.utils.TestDataUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarService carService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;
    @Mock
    private TelegramNotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;
    private Car car;
    private CarDto carDto;
    private User user;
    private Rental rental;
    private RentalDto rentalDto;
    private CreateRentalRequestDto createRentalRequestDto;
    private ReturnRentalRequestDto returnRentalRequestDto;

    @BeforeEach
    void setUp() {
        car = TestDataUtil.getTestCar();
        carDto = TestDataUtil.getTestCarDtoAvailable();
        user = TestDataUtil.getTestUser();
        rental = TestDataUtil.getTestRental();
        rentalDto = TestDataUtil.getTestRentalDto();
        createRentalRequestDto = TestDataUtil.getTestCreateRentalRequestDto();
        returnRentalRequestDto = TestDataUtil.getTestReturnRentalRequestDto();
    }

    @Test
    void createRental_ValidInput_ok() {
        when(carService.checkCarAvailability(any()))
                .thenReturn(car);
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);
        when(rentalMapper.toModelFromCreateDto(any(CreateRentalRequestDto.class)))
                .thenReturn(rental);

        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        RentalDto result = rentalService.createRental(user, createRentalRequestDto);

        assertNotNull(result);
        assertEquals(rentalDto, result);
        verify(carService).checkCarAvailability(TestDataUtil.TEST_CAR_ID_AVAILABLE);
        verify(rentalMapper).toModelFromCreateDto(createRentalRequestDto);
        verify(rentalRepository).save(rental);
        verify(rentalMapper).toDto(rental);
    }

    @MockitoSettings(strictness = Strictness.LENIENT)
    @Test
    void createRental_CarNotAvailable_ThrowsCarNotAvailableException() {
        car.setInventory(TestDataUtil.TEST_CAR_INVENTORY_NOT_AVAILABLE);
        when(carService.checkCarAvailability(any()))
                .thenThrow(new CarNotAvailableException("This car is not available"));
        assertThrows(CarNotAvailableException.class,
                () -> rentalService.createRental(user, createRentalRequestDto)
        );
        verify(carService).checkCarAvailability(TestDataUtil.TEST_CAR_ID_AVAILABLE);

        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void findById_ExistingId_ok() {
        when(rentalRepository.findById(TestDataUtil.TEST_RENTAL_ID))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);
        RentalDto result = rentalService.findById(TestDataUtil.TEST_RENTAL_ID);

        assertNotNull(result);
        assertEquals(rentalDto, result);
        verify(rentalRepository).findById(TestDataUtil.TEST_RENTAL_ID);
        verify(rentalMapper).toDto(rental);
    }

    @Test
    void findById_NonExistingId_ThrowsEntityNotFoundException() {
        when(rentalRepository.findById(TestDataUtil.TEST_RENTAL_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rentalService.findById(TestDataUtil.TEST_RENTAL_ID),
                "Can't find rental by id " + TestDataUtil.TEST_RENTAL_ID);
    }

    @Test
    void closeRental_ExistingId_ReturnsClosedRentalDto() {
        Rental closedRental = new Rental()
                .setId(TestDataUtil.TEST_RENTAL_ID)
                .setCar(car)
                .setUser(user)
                .setRentalDate(TestDataUtil.TEST_RENTAL_DATE)
                .setReturnDate(TestDataUtil.TEST_RETURN_DATE)
                .setActualReturnDate(TestDataUtil.TEST_ACTUAL_RETURN_DATE);

        when(rentalRepository.findRentalByIdAndActualReturnDateIsNull(TestDataUtil.TEST_RENTAL_ID))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(closedRental);
        when(rentalMapper.toDto(closedRental)).thenReturn(TestDataUtil.getTestClosedRentalDto());

        RentalDto result = rentalService.closeRental(TestDataUtil.TEST_RENTAL_ID,
                returnRentalRequestDto);

        assertNotNull(result);
        assertEquals(TestDataUtil.getTestClosedRentalDto(), result);

        verify(rentalRepository).save(any(Rental.class));
        verify(rentalMapper).toDto(closedRental);
    }

    @Test
    void closeRental_NonExistingId_ThrowsEntityNotFoundException() {
        //when(rentalRepository.findById(TestDataUtil.TEST_RENTAL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> rentalService.closeRental(TestDataUtil.TEST_RENTAL_ID,
                        returnRentalRequestDto),
                "Can't find rental by id " + TestDataUtil.TEST_RENTAL_ID);
    }

    @Test
    void findByUserIdAndActive_ValidInput_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Rental> rentals = List.of(rental);
        Page<Rental> page = new PageImpl<>(rentals);
        when(rentalRepository.findRentalsByUserIdAndActualReturnDateIsNull(
                TestDataUtil.TEST_USER_ID, pageable))
                .thenReturn(page);
        when(authenticationService.getCurrentUserId())
                .thenReturn(Optional.of(TestDataUtil.TEST_USER_ID));
        when(rentalMapper.toDto(any(Rental.class))).thenReturn(rentalDto);
        List<RentalDto> result = rentalService.findByUserIdAndActive(
                TestDataUtil.TEST_USER_ID, true, pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rentalDto, result.get(0));
        verify(rentalRepository).findRentalsByUserIdAndActualReturnDateIsNull(
                TestDataUtil.TEST_USER_ID, pageable);
        verify(rentalMapper).toDto(rental);
    }

    @Test
    void checkOverdueRentals_OverdueRentalExists_CallsRepository() {
        List<Rental> overdueRentals = List.of(TestDataUtil.getTestOverdueRental());
        when(rentalRepository
                .findRentalsByRentalDateBeforeAndActualReturnDateIsNull(any(LocalDate.class)))
                .thenReturn(overdueRentals);

        rentalService.checkOverdueRentals();

        verify(rentalRepository)
                .findRentalsByRentalDateBeforeAndActualReturnDateIsNull(any(LocalDate.class));
    }
}
