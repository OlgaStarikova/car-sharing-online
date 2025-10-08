package com.example.carsharingonline.utils;

import com.example.carsharingonline.dto.car.CarDto;
import com.example.carsharingonline.dto.car.CreateCarRequestDto;
import com.example.carsharingonline.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.rental.CreateRentalRequestDto;
import com.example.carsharingonline.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.payment.PaymentResponseDto;
import com.example.carsharingonline.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingonline.dto.rental.RentalDto;
import com.example.carsharingonline.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingonline.dto.user.UserLoginRequestDto;
import com.example.carsharingonline.dto.user.UserLoginResponseDto;
import com.example.carsharingonline.dto.user.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.user.UserResponseDto;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class TestDataUtil {
    public static final Long TEST_CAR_ID_AVAILABLE = 1L;
    public static final Long TEST_CAR_ID_NOT_AVAILABLE = 2L;
    public static final String TEST_CAR_MODEL = "Civic";
    public static final String TEST_CAR_BRAND = "Honda";
    public static final Car.CarBodyType TEST_CAR_BODY_TYPE = Car.CarBodyType.SEDAN;
    public static final String TEST_CAR_BODY_TYPE_STRING = "SEDAN";
    public static final int TEST_CAR_INVENTORY_AVAILABLE = 1;
    public static final int TEST_CAR_INVENTORY_NOT_AVAILABLE = 0;
    public static final BigDecimal TEST_CAR_DAILY_FEE = BigDecimal.valueOf(50.00);
    public static final boolean TEST_CAR_IS_DELETED = false;
    public static final String TEST_CAR_MODEL_NEW = "Civic";
    public static final String TEST_CAR_BRAND_NEW = "Honda";
    public static final String TEST_CAR_BODY_TYPE_STRING_NEW = "SEDAN";
    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_REGISTERED_USER_ID = 3L;
    public static final Long TEST_NEW_USER_ID = 4L;
    public static final String TEST_REGISTERED_USER_EMAIL = "registered@example.com";
    public static final String TEST_NEW_USER_EMAIL = "newuser@example.com";
    public static final String TEST_USER_PASSWORD = "123456789";
    public static final String TEST_USER_FIRST_NAME = "John";
    public static final String TEST_USER_LAST_NAME = "Smith";
    public static final String TEST_TOKEN = "sss";
    public static final Long TEST_ADMIN_ID = 2L;
    public static final String TEST_ADMIN_EMAIL = "admin@example.com";
    public static final Long TEST_RENTAL_ID = 1L;
    public static final Long TEST_RENTAL_ID_NEW = 2L;
    public static final LocalDate TEST_RENTAL_DATE = LocalDate.now();
    public static final LocalDate TEST_RETURN_DATE = LocalDate.now().plusDays(2);
    public static final LocalDate TEST_ACTUAL_RETURN_DATE = LocalDate.now().plusDays(2);
    public static final LocalDate TEST_OVERDUE_RETURN_DATE = LocalDate.now().plusDays(3);
    public static final boolean TEST_RENTAL_IS_ACTIVE = true;
    public static final Long TEST_PAYMENT_ID = 1L;
    public static final String TEST_SESSION_ID = "session_123";
    public static final String TEST_SESSION_URL = "https://checkout.stripe.com/session_123";
    public static final BigDecimal TEST_AMOUNT_TO_PAY = BigDecimal.valueOf(100.00);
    public static final Payment.Type TEST_PAYMENT_TYPE = Payment.Type.PAYMENT;
    public static final Payment.Status TEST_PAYMENT_STATUS = Payment.Status.PENDING;

    private static final CreateCarRequestDto createCarRequestDto = new CreateCarRequestDto(
            TEST_CAR_MODEL,
            TEST_CAR_BRAND,
            TEST_CAR_BODY_TYPE_STRING,
            TEST_CAR_DAILY_FEE
    );

    private static final CreateCarRequestDto createCarRequestDtoNew = new CreateCarRequestDto(
            TEST_CAR_MODEL_NEW,
            TEST_CAR_BRAND_NEW,
            TEST_CAR_BODY_TYPE_STRING_NEW,
            TEST_CAR_DAILY_FEE
    );

    private static final CarDto carDtoAvailable = new CarDto(
            TEST_CAR_MODEL,
            TEST_CAR_BRAND,
            TEST_CAR_BODY_TYPE_STRING,
            TEST_CAR_INVENTORY_AVAILABLE,
            TEST_CAR_DAILY_FEE
    );
    private static final CarDto carDtoNotAvailable = new CarDto(
            TEST_CAR_MODEL,
            TEST_CAR_BRAND,
            TEST_CAR_BODY_TYPE_STRING,
            TEST_CAR_INVENTORY_NOT_AVAILABLE,
            TEST_CAR_DAILY_FEE
    );
    private static final CarDto carDtoNew = new CarDto(
            TEST_CAR_MODEL_NEW,
            TEST_CAR_BRAND_NEW,
            TEST_CAR_BODY_TYPE_STRING_NEW,
            TEST_CAR_INVENTORY_AVAILABLE,
            TEST_CAR_DAILY_FEE
    );

    private static final Car car = new Car()
            .setId(TEST_CAR_ID_AVAILABLE)
            .setModel(TEST_CAR_MODEL)
            .setBrand(TEST_CAR_BRAND)
            .setCarBodyType(TEST_CAR_BODY_TYPE)
            .setInventory(TEST_CAR_INVENTORY_AVAILABLE)
            .setDaylyFee(TEST_CAR_DAILY_FEE)
            .setDeleted(TEST_CAR_IS_DELETED);

    private static final Role userRole = new Role();

    static {
        userRole.setRole(Role.RoleName.CUSTOMER);
    }

    private static final Role adminRole = new Role();

    static {
        adminRole.setRole(Role.RoleName.MANAGER);
    }

    private static final User user = new User()
            .setId(TEST_USER_ID)
            .setEmail(TEST_REGISTERED_USER_EMAIL)
            .setRoles(Set.of(userRole));

    private static final User adminUser = new User()
            .setId(TEST_ADMIN_ID)
            .setEmail(TEST_ADMIN_EMAIL)
            .setRoles(Set.of(adminRole));

    private static final UserRegistrationRequestDto userRegistrationRequestDto =
            new UserRegistrationRequestDto(
                    TEST_NEW_USER_EMAIL,
                    TEST_USER_PASSWORD,
                    TEST_USER_PASSWORD,
                    TEST_USER_FIRST_NAME,
                    TEST_USER_LAST_NAME
            );
    private static final UserLoginRequestDto userLoginRequestDto =
            new UserLoginRequestDto(
                    TEST_REGISTERED_USER_EMAIL,
                    TEST_USER_PASSWORD
            );
    private static final UserLoginResponseDto userLoginResponseDto =
            new UserLoginResponseDto(
                    TEST_TOKEN
            );

    private static final UserResponseDto userResponseDto =
            new UserResponseDto(
                    TEST_NEW_USER_ID,
                    TEST_NEW_USER_EMAIL,
                    TEST_USER_FIRST_NAME,
                    TEST_USER_LAST_NAME,
                    Set.of(Role.RoleName.MANAGER.toString())
            );

    private static final CreateRentalRequestDto createRentalRequestDto =
            new CreateRentalRequestDto(
                    TEST_RENTAL_DATE,
                    TEST_RETURN_DATE,
                    TEST_CAR_ID_AVAILABLE
            );

    private static final ReturnRentalRequestDto returnRentalRequestDto =
            new ReturnRentalRequestDto(
                    TEST_ACTUAL_RETURN_DATE,
                    TEST_CAR_ID_NOT_AVAILABLE
            );

    private static final RentalDto rentalDto = new RentalDto(
            TEST_RENTAL_DATE,
            TEST_RETURN_DATE,
            null,
            carDtoAvailable
    );

    private static final RentalDto closedRentalDto = new RentalDto(
            TEST_RENTAL_DATE,
            TEST_RETURN_DATE,
            TEST_ACTUAL_RETURN_DATE,
            carDtoAvailable
    );

    private static final Rental rental = new Rental()
            .setId(TEST_RENTAL_ID)
            .setRentalDate(TEST_RENTAL_DATE)
            .setReturnDate(TEST_RETURN_DATE)
            .setActualReturnDate(null)
            .setCar(car)
            .setUser(user);

    private static final Rental overdueRental = new Rental()
            .setId(TEST_RENTAL_ID)
            .setRentalDate(TEST_RENTAL_DATE)
            .setReturnDate(TEST_OVERDUE_RETURN_DATE)
            .setActualReturnDate(null)
            .setCar(car)
            .setUser(user);

    private static final CreatePaymentRequestDto createPaymentRequestDto =
            new CreatePaymentRequestDto(
                    TEST_RENTAL_ID,
                    TEST_PAYMENT_TYPE
            );

    private static final Payment payment = new Payment()
            .setId(TEST_PAYMENT_ID)
            .setStatus(TEST_PAYMENT_STATUS)
            .setType(TEST_PAYMENT_TYPE)
            .setRental(rental)
            .setSessionUrl(TEST_SESSION_URL)
            .setSessionId(TEST_SESSION_ID)
            .setAmountToPay(TEST_AMOUNT_TO_PAY);

    private static final PaymentDetailedResponseDto paymentDetailedResponseDto
            = new PaymentDetailedResponseDto(
            TEST_PAYMENT_ID,
            TEST_PAYMENT_STATUS,
            TEST_PAYMENT_TYPE,
            TEST_RENTAL_ID,
            TEST_SESSION_URL,
            TEST_SESSION_ID,
            TEST_AMOUNT_TO_PAY
    );

    private static final PaymentResponseDto paymentResponseDto = new PaymentResponseDto(
            TEST_SESSION_URL,
            TEST_SESSION_ID
    );

    private static final PaymentStatusResponseDto paymentStatusResponseDto =
            new PaymentStatusResponseDto(
                    TEST_SESSION_ID,
                    Payment.Status.PAID
            );

    public static CreateCarRequestDto getTestCreateCarRequestDto() {
        return createCarRequestDto;
    }

    public static CreateCarRequestDto getTestCreateCarRequestDtoNew() {
        return createCarRequestDtoNew;
    }

    public static CarDto getTestCarDtoAvailable() {
        return carDtoAvailable;
    }

    public static CarDto getTestCarDtoNotAvailable() {
        return carDtoNotAvailable;
    }

    public static Car getTestCar() {
        return car;
    }

    public static User getTestUser() {
        return user;
    }

    public static User getTestAdminUser() {
        return adminUser;
    }

    public static UserRegistrationRequestDto getTestUserRegistrationRequestDto() {
        return userRegistrationRequestDto;
    }

    public static UserLoginRequestDto getTestUserLoginRequestDto() {
        return userLoginRequestDto;
    }

    public static UserLoginResponseDto getTestUserLoginResponseDto() {
        return userLoginResponseDto;
    }

    public static UserResponseDto getTestUserResponseDto() {
        return userResponseDto;
    }

    public static CreateRentalRequestDto getTestCreateRentalRequestDto() {
        return createRentalRequestDto;
    }

    public static ReturnRentalRequestDto getTestReturnRentalRequestDto() {
        return returnRentalRequestDto;
    }

    public static RentalDto getTestRentalDto() {
        return rentalDto;
    }

    public static RentalDto getTestClosedRentalDto() {
        return closedRentalDto;
    }

    public static Rental getTestRental() {
        return rental;
    }

    public static Rental getTestOverdueRental() {
        return overdueRental;
    }

    public static CreatePaymentRequestDto getTestCreatePaymentRequestDto() {
        return createPaymentRequestDto;
    }

    public static Payment getTestPayment() {
        return payment;
    }

    public static PaymentDetailedResponseDto getTestPaymentDetailedResponseDto() {
        return paymentDetailedResponseDto;
    }

    public static PaymentResponseDto getTestPaymentResponseDto() {
        return paymentResponseDto;
    }

    public static PaymentStatusResponseDto getTestPaymentStatusResponseDto() {
        return paymentStatusResponseDto;
    }
}
