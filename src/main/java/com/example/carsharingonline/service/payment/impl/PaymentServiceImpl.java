package com.example.carsharingonline.service.payment.impl;

import com.example.carsharingonline.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.payment.PaymentResponseDto;
import com.example.carsharingonline.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.exception.PaymentException;
import com.example.carsharingonline.mapper.PaymentMapper;
import com.example.carsharingonline.model.Payment;
import com.example.carsharingonline.model.Rental;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.PaymentRepository;
import com.example.carsharingonline.repository.RentalRepository;
import com.example.carsharingonline.service.notification.telegram.TelegramNotificationService;
import com.example.carsharingonline.service.notification.template.NotificationTemplates;
import com.example.carsharingonline.service.notification.template.NotificationType;
import com.example.carsharingonline.service.payment.PaymentService;
import com.example.carsharingonline.service.payment.provider.stripe.StripeService;
import com.example.carsharingonline.service.payment.strategy.CalculationService;
import com.example.carsharingonline.service.payment.strategy.CalculationServiceStrategy;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
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
        log.debug("Preparing to get all payments for user={}", user.getEmail());
        if (!user.getRoles()
                .stream()
                .anyMatch(role -> role.getRole() == Role.RoleName.MANAGER)) {
            if (!Objects.equals(user.getId(), id)) {
                log.error("❌ Access denied: user {} (id={}) tried to access payments of user {}",
                        user.getEmail(), user.getId(), id);
                throw new AccessDeniedException("This user with id: " + user.getId()
                        + " can't see payments of other users");
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
        Rental rental = rentalRepository.findById(requestDto.rentalId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find a rental by id: "
                        + requestDto.rentalId())
        );
        CalculationService calculationService = calculationServiceStrategy
                .getCalculationService(requestDto.type());
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
            throw new EntityNotFoundException("??Can't find a payment by sessionId: " + sessionId);
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
                .setType(requestDto.type())
                .setRental(rental)
                .setSessionUrl(session.getUrl())
                .setSessionId(session.getId())
                .setAmountToPay(BigDecimal.valueOf(session.getAmountTotal()));
    }
}
