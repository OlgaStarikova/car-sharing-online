package com.example.carsharingonline.dto;

import com.example.carsharingonline.model.Payment;

public record PaymentStatusResponseDto(
        String sessionId,
        Payment.Status status
) {
}
