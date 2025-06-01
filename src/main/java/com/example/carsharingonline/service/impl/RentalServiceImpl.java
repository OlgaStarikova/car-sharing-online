package com.example.carsharingonline.service.impl;

import com.example.carsharingonline.dto.CreateRentalRequestDto;
import com.example.carsharingonline.dto.RentalDto;
import com.example.carsharingonline.dto.ReturnRentalRequestDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.mapper.RentalMapper;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.RentalRepository;
import com.example.carsharingonline.security.AuthenticationService;
import com.example.carsharingonline.service.CarService;
import com.example.carsharingonline.service.RentalService;
import com.example.carsharingonline.service.UserService;
import com.example.carsharingonline.service.notification.NotificationTemplates;
import com.example.carsharingonline.service.notification.NotificationType;
import com.example.carsharingonline.service.notification.TelegramNotificationService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RentalServiceImpl implements RentalService {
    private static final int INCREASE_VALUE = 1;
    private static final int DECREASE_VALUE = -1;
    private static final int DAYS_FOR_NOTICE_BEFORE_OVERDUE = 1;
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarService carService;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final TelegramNotificationService notificationService;

    @Override
    public RentalDto createRental(User user, CreateRentalRequestDto requestDto) {
        Car car = carService.checkCarAvailability(requestDto.carId());
        RentalDto rentalDto = Optional.ofNullable(requestDto)
                .map(rentalMapper::toModelFromCreateDto)
                .map(rental -> rental.setUser(user))
                .map(rental -> rental.setCar(car))
                .map(rentalRepository::save)
                .map(rentalMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Input parameters can't be null"));
        carService.updateCarInventory(requestDto.carId(), DECREASE_VALUE);
        notificationService.sendMessageAdmin(NotificationTemplates.getTemplate(
                NotificationType.NEW_RENTAL,
                car.getId(),
                car.getModel(),
                user.getEmail(),
                rentalDto.rentalDate(),
                rentalDto.returnDate()));
        return rentalDto;
    }

    @Override
    public List<RentalDto> findByUserIdAndActive(Long userId, Boolean isActive, Pageable pageable) {
        userId = resolveUserIdOrThrow(userId);
        Page<Rental> rentals;
        if (userId == null) {
            if (isActive == null) {
                rentals = rentalRepository.findAll(pageable);
            } else {
                if (isActive) {
                    rentals = rentalRepository.findRentalsByActualReturnDateIsNull(pageable);
                } else {
                    rentals = rentalRepository.findRentalsByActualReturnDateExists(pageable);
                }
            }
        } else {
            if (isActive == null) {
                rentals = rentalRepository.findRentalsByUserId(userId, pageable);
            } else {
                if (isActive) {
                    rentals = rentalRepository
                            .findRentalsByUserIdAndActualReturnDateIsNull(userId, pageable);
                } else {
                    rentals = rentalRepository
                            .findRentalsByUserIdAndActualReturnDateExists(userId, pageable);
                }
            }
        }
        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    private Long resolveUserIdOrThrow(Long userId) {
        if (userId != null) {
            userService.findUserById(userId);
        }
        Boolean isRoleHasAdmin = authenticationService.hasRole("ADMIN");
        Long currentUserId = authenticationService.getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("Can't identify "
                        + "the user"));

        if (userId == null) {
            if (isRoleHasAdmin) {
                return null;
            } else {
                return currentUserId;
            }
        } else {
            if (isRoleHasAdmin) {
                return userId;
            } else {
                if (userId.equals(currentUserId)) {
                    return userId;
                } else {
                    throw new AccessDeniedException("Access Denied: Can't "
                            + "view another user's data");
                }
            }
        }
    }

    @Override
    public RentalDto findById(Long id) {
        return rentalMapper.toDto(rentalRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find rental by id " + id)));
    }

    @Override
    public RentalDto closeRental(Long id, ReturnRentalRequestDto requestDto) {
        Rental rental = rentalRepository.findRentalByIdAndActualReturnDateIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id " + id));
        rental.setActualReturnDate(requestDto.actualReturnDate());
        Rental savedRental = rentalRepository.save(rental);
        carService.updateCarInventory(requestDto.carId(), INCREASE_VALUE);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    @Scheduled(cron = "0 10 22 * * *")
    public void checkOverdueRentals() {
        LocalDate dateOverdue = LocalDate.now().plusDays(DAYS_FOR_NOTICE_BEFORE_OVERDUE);

        List<Rental> overdueRentals = rentalRepository
                .findRentalsByRentalDateBeforeAndActualReturnDateIsNull(dateOverdue);

        if (overdueRentals.isEmpty()) {
            notificationService
                    .sendMessageAdmin("✅ No overdue rentals today!");
            return;
        }
        for (Rental rental : overdueRentals) {
            notificationService.sendMessageAdmin(NotificationTemplates.getTemplate(
                    NotificationType.OVERDUE_RENTAL,
                    rental.getCar().getId(),
                    rental.getUser().getFirstName() + " " + rental.getUser().getLastName(),
                    rental.getUser().getEmail(),
                    rental.getCar().getModel(),
                    rental.getReturnDate(),
                    ChronoUnit.DAYS.between(LocalDate.now(), rental.getReturnDate())));
        }
    }
}
