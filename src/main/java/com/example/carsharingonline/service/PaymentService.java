package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.PaymentResponseDto;
import com.example.carsharingonline.dto.PaymentStatusResponseDto;
import com.example.carsharingonline.model.User;
import java.util.List;

public interface PaymentService {
    List<PaymentDetailedResponseDto> getAll(User user, Long id);

    PaymentResponseDto createPaymentSession(CreatePaymentRequestDto requestDto);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
