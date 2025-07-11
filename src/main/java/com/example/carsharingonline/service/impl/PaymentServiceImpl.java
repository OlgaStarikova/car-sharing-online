package com.example.carsharingonline.service.impl;

import com.example.carsharingonline.dto.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.PaymentResponseDto;
import com.example.carsharingonline.dto.PaymentStatusResponseDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.exception.PaymentException;
import com.example.carsharingonline.mapper.PaymentMapper;
import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.PaymentRepository;
import com.example.carsharingonline.repository.RentalRepository;
import com.example.carsharingonline.service.PaymentService;
import com.example.carsharingonline.service.StripeService;
import com.example.carsharingonline.service.notification.NotificationTemplates;
import com.example.carsharingonline.service.notification.NotificationType;
import com.example.carsharingonline.service.notification.TelegramNotificationService;
import com.example.carsharingonline.service.strategy.CalculationService;
import com.example.carsharingonline.service.strategy.CalculationServiceStrategy;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String COMPLETED_STATUS = "complete";
    @Value("${stripe.key}")
    private String secretKey;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final CalculationServiceStrategy calculationServiceStrategy;
    private final PaymentMapper paymentMapper;
    private final TelegramNotificationService notificationService;

    @Override
    public List<PaymentDetailedResponseDto> getAll(User user, Long id) {
        if (!user.getRoles().contains(Role.RoleName.ADMIN)) {
            if (!Objects.equals(user.getId(), id)) {
                throw new AccessDeniedException("This user with id: " + user.getId()
                        + " can't see payments of other customers");
            }
            return paymentRepository.findAllByRental_User_Id(id).stream()
                    .map(paymentMapper::toDetailedDto)
                    .toList();
        }
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDetailedDto)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponseDto createPaymentSession(CreatePaymentRequestDto requestDto) {
        Stripe.apiKey = secretKey;
        Rental rental = rentalRepository.findById(requestDto.getRentalId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: "
                        + requestDto.getRentalId())
        );
        CalculationService calculationService = calculationServiceStrategy
                .getCalculationService(requestDto.getType());
        BigDecimal amount = calculationService.calculateAmount(rental);
        Session session = stripeService.createSession(amount);
        return paymentMapper.toDto(paymentRepository
                .save(preparePayment(session, requestDto, rental)));
    }

    @Override
    @Transactional
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        Payment payment = paymentRepository.findPaymentBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId)
        );
        try {
            Session session = Session.retrieve(sessionId);
            if (session.getStatus().equals(COMPLETED_STATUS)) {
                payment.setStatus(Payment.Status.PAID);
                Payment savedPayment = paymentRepository.save(payment);
                String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_SUCCESS,
                        payment.getId(),
                        payment.getAmountToPay(),
                        payment.getRental().getId());
                notificationService.sendMessageAdmin(message);
                return paymentMapper.toStatusDto(savedPayment);
            } else {
                throw new PaymentException("Payment checkout session with id: "
                        + sessionId + " is not completed");
            }
        } catch (StripeException e) {
            throw new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId);
        }
    }

    @Override
    @Transactional
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = paymentRepository.findPaymentBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find a payment by sessionId: " + sessionId)
        );
        payment.setStatus(Payment.Status.CANCEL);
        Payment savedPayment = paymentRepository.save(payment);
        String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_FAILED,
                payment.getId(),
                payment.getRental().getId());
        notificationService.sendMessageAdmin(message);
        return paymentMapper.toStatusDto(savedPayment);
    }

    private static Payment preparePayment(Session session,
                                          CreatePaymentRequestDto requestDto,
                                          Rental rental) {
        return new Payment()
                .setStatus(Payment.Status.PENDING)
                .setType(requestDto.getType())
                .setRental(rental)
                .setSessionUrl(session.getUrl())
                .setSessionId(session.getId())
                .setAmountToPay(BigDecimal.valueOf(session.getAmountTotal()));
    }
}
