
package com.example.carsharingonline.controller;

import com.example.carsharingonline.dto.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.PaymentResponseDto;
import com.example.carsharingonline.dto.PaymentStatusResponseDto;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registered/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @Operation(summary = "Get payments")
    public List<PaymentDetailedResponseDto> getAll(Authentication authentication,
                                                   @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        return paymentService.getAll(user, id);
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping
    @Operation(summary = "Create payment session")
    public PaymentResponseDto createPaymentSession(
            @RequestBody @Valid CreatePaymentRequestDto requestDto) {
        return paymentService.createPaymentSession(requestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/success")
    @Operation(summary = "Handle successful payment")
    public PaymentStatusResponseDto handleSuccess(
            @RequestParam String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/cancel")
    @Operation(summary = "Handle cancel payment")
    public PaymentStatusResponseDto handleCancel(
            @RequestParam String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
