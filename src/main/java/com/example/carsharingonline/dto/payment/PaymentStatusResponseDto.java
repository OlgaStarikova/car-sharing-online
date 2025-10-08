package com.example.carsharingonline.dto.payment;

import com.example.carsharingonline.model.Payment;

public record PaymentStatusResponseDto(
        String sessionId,
        Payment.Status status
) {
}
