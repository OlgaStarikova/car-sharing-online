package com.example.carsharingonline.dto.payment;

public record PaymentResponseDto(
        String sessionId,
        String sessionUrl
) {
}
