package com.example.carsharingonline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingonline.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.payment.PaymentResponseDto;
import com.example.carsharingonline.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.exception.PaymentException;
import com.example.carsharingonline.mapper.PaymentMapper;
import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.PaymentRepository;
import com.example.carsharingonline.repository.RentalRepository;
import com.example.carsharingonline.service.payment.impl.PaymentServiceImpl;
import com.example.carsharingonline.service.notification.telegram.TelegramNotificationService;
import com.example.carsharingonline.service.payment.provider.stripe.StripeService;
import com.example.carsharingonline.service.payment.strategy.CalculationService;
import com.example.carsharingonline.service.payment.strategy.CalculationServiceStrategy;
import com.example.carsharingonline.utils.TestDataUtil;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final String COMPLETED_STATUS = "complete";

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private StripeService stripeService;

    @Mock
    private CalculationServiceStrategy calculationServiceStrategy;

    @Mock
    private CalculationService calculationService;

    @Mock
    private TelegramNotificationService notificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private User adminUser;
    private Rental rental;
    private Payment payment;
    private CreatePaymentRequestDto createPaymentRequestDto;
    private PaymentDetailedResponseDto paymentDetailedResponseDto;
    private PaymentResponseDto paymentResponseDto;
    private PaymentStatusResponseDto paymentStatusResponseDto;

    @BeforeEach
    void setUp() {
        user = TestDataUtil.getTestUser();
        adminUser = TestDataUtil.getTestAdminUser();
        rental = TestDataUtil.getTestRental();
        payment = TestDataUtil.getTestPayment();
        createPaymentRequestDto = TestDataUtil.getTestCreatePaymentRequestDto();
        paymentDetailedResponseDto = TestDataUtil.getTestPaymentDetailedResponseDto();
        paymentResponseDto = TestDataUtil.getTestPaymentResponseDto();
        paymentStatusResponseDto = TestDataUtil.getTestPaymentStatusResponseDto();
    }

    @Test
    void getAll_AdminUser_ReturnsAllPayments() {
        List<Payment> payments = List.of(payment);
        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentMapper.toDetailedDto(any(Payment.class)))
                .thenReturn(paymentDetailedResponseDto);
        List<PaymentDetailedResponseDto> result = paymentService
                .getAll(adminUser, TestDataUtil.TEST_USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentDetailedResponseDto, result.get(0));
        verify(paymentRepository).findAll();
        verify(paymentMapper).toDetailedDto(payment);
    }

    @Test
    void getAll_NonAdminUserOwnId_ReturnsUserPayments() {
        List<Payment> payments = List.of(payment);
        when(paymentRepository.findAllByRental_User_Id(TestDataUtil.TEST_USER_ID))
                .thenReturn(payments);
        when(paymentMapper.toDetailedDto(any(Payment.class)))
                .thenReturn(paymentDetailedResponseDto);
        List<PaymentDetailedResponseDto> result = paymentService
                .getAll(user, TestDataUtil.TEST_USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentDetailedResponseDto, result.get(0));
        verify(paymentRepository)
                .findAllByRental_User_Id(TestDataUtil.TEST_USER_ID);
        verify(paymentMapper).toDetailedDto(payment);
    }

    @Test
    void getAll_NonAdminUserDifferentId_ThrowsAccessDeniedException() {
        Long differentUserId = 2L;

        assertThrows(AccessDeniedException.class,
                () -> paymentService.getAll(user, differentUserId),
                "This user with id: " + TestDataUtil.TEST_USER_ID
                        + " can't see payments of other users");
    }

    @Test
    void getAll_EmptyPayments_ReturnsEmptyList() {
        when(paymentRepository.findAllByRental_User_Id(TestDataUtil.TEST_USER_ID))
                .thenReturn(Collections.emptyList());

        List<PaymentDetailedResponseDto> result = paymentService
                .getAll(user, TestDataUtil.TEST_USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentRepository).findAllByRental_User_Id(TestDataUtil.TEST_USER_ID);
    }

    @Test
    void createPaymentSession_ValidInput_ReturnsPaymentResponseDto() throws StripeException {
        Session session = mock(Session.class);
        when(session.getUrl()).thenReturn(TestDataUtil.TEST_SESSION_URL);
        when(session.getId()).thenReturn(TestDataUtil.TEST_SESSION_ID);
        when(session.getAmountTotal()).thenReturn(TestDataUtil.TEST_AMOUNT_TO_PAY.longValue());
        when(rentalRepository.findById(TestDataUtil.TEST_RENTAL_ID))
                .thenReturn(Optional.of(rental));
        when(stripeService.createSession(any(BigDecimal.class))).thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(calculationServiceStrategy.getCalculationService(any(Payment.Type.class)))
                .thenReturn(calculationService);
        when(calculationService.calculateAmount(any(Rental.class)))
                .thenReturn(TestDataUtil.TEST_AMOUNT_TO_PAY);
        when(paymentMapper.toDto(any(Payment.class))).thenReturn(paymentResponseDto);
        PaymentResponseDto result = paymentService.createPaymentSession(createPaymentRequestDto);

        assertNotNull(result);
        assertEquals(paymentResponseDto, result);
        verify(rentalRepository).findById(TestDataUtil.TEST_RENTAL_ID);
        verify(calculationServiceStrategy).getCalculationService(TestDataUtil.TEST_PAYMENT_TYPE);
        verify(calculationService).calculateAmount(rental);
        verify(stripeService).createSession(TestDataUtil.TEST_AMOUNT_TO_PAY);
        verify(paymentRepository).save(any(Payment.class));
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void createPaymentSession_NonExistingRental_ThrowsEntityNotFoundException() {
        when(rentalRepository.findById(TestDataUtil.TEST_RENTAL_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.createPaymentSession(createPaymentRequestDto),
                "Can't find a rental by id: " + TestDataUtil.TEST_RENTAL_ID);
        verify(rentalRepository).findById(TestDataUtil.TEST_RENTAL_ID);
        verify(stripeService, never()).createSession(any());
    }

    @Test
    void handleSuccess_CompletedSession_ReturnsPaymentStatusResponseDto() throws StripeException {
        Session session = mock(Session.class);
        when(session.getStatus()).thenReturn(COMPLETED_STATUS);
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toStatusDto(any(Payment.class))).thenReturn(paymentStatusResponseDto);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        try (var mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.retrieve(TestDataUtil.TEST_SESSION_ID))
                    .thenReturn(session);

            PaymentStatusResponseDto result = paymentService
                    .handleSuccess(TestDataUtil.TEST_SESSION_ID);

            assertNotNull(result);
            assertEquals(paymentStatusResponseDto, result);
            verify(paymentRepository).findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID);
            verify(paymentRepository).save(payment);
            verify(notificationService).sendMessageAdmin(anyString());
            verify(paymentMapper).toStatusDto(payment);
        }
    }

    @Test
    void handleSuccess_NonExistingSession_ThrowsEntityNotFoundException() {
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleSuccess(TestDataUtil.TEST_SESSION_ID),
                "Can't find a payment by sessionId: " + TestDataUtil.TEST_SESSION_ID);
    }

    @Test
    void handleSuccess_NonCompletedSession_ThrowsPaymentException() throws StripeException {
        Session session = mock(Session.class);
        when(session.getStatus()).thenReturn("open");
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.of(payment));
        try (var mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.retrieve(TestDataUtil.TEST_SESSION_ID))
                    .thenReturn(session);

            assertThrows(PaymentException.class,
                    () -> paymentService.handleSuccess(TestDataUtil.TEST_SESSION_ID),
                    "Payment checkout session with id: "
                            + TestDataUtil.TEST_SESSION_ID + " is not completed");
        }
    }

    @Test
    void handleSuccess_StripeException_ThrowsEntityNotFoundException() throws StripeException {
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.of(payment));
        try (var mockedStatic = mockStatic(Session.class)) {
            mockedStatic.when(() -> Session.retrieve(TestDataUtil.TEST_SESSION_ID))
                    .thenThrow(new ApiException("API error", null, null, 400, null));

            assertThrows(EntityNotFoundException.class,
                    () -> paymentService.handleSuccess(TestDataUtil.TEST_SESSION_ID),
                    "Can't find a payment by sessionId: "
                            + TestDataUtil.TEST_SESSION_ID);
        }
    }

    @Test
    void handleCancel_ValidSession_ReturnsPaymentStatusResponseDto() {
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentMapper.toStatusDto(any(Payment.class)))
                .thenReturn(paymentStatusResponseDto);

        PaymentStatusResponseDto result = paymentService
                .handleCancel(TestDataUtil.TEST_SESSION_ID);

        assertNotNull(result);
        assertEquals(paymentStatusResponseDto, result);
        verify(paymentRepository).findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID);
        verify(paymentRepository).save(payment);
        verify(notificationService).sendMessageAdmin(anyString());
        verify(paymentMapper).toStatusDto(payment);
    }

    @Test
    void handleCancel_NonExistingSession_ThrowsEntityNotFoundException() {
        when(paymentRepository.findPaymentBySessionId(TestDataUtil.TEST_SESSION_ID))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> paymentService.handleCancel(TestDataUtil.TEST_SESSION_ID),
                "Can't find a payment by sessionId: "
                        + TestDataUtil.TEST_SESSION_ID);
    }
}
