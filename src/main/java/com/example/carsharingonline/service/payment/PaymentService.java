package com.example.carsharingonline.service.payment;

import com.example.carsharingonline.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.payment.PaymentResponseDto;
import com.example.carsharingonline.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingonline.model.User;
import java.util.List;

public interface PaymentService {
    List<PaymentDetailedResponseDto> getAll(User user, Long id);

    PaymentResponseDto createPaymentSession(CreatePaymentRequestDto requestDto);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
