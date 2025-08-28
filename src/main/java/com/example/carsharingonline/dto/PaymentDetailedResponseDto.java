package com.example.carsharingonline.dto;

import com.example.carsharingonline.model.Payment;
import java.math.BigDecimal;

public record PaymentDetailedResponseDto(
        Long id,
        Payment.Status status,
        Payment.Type type,
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {
}
