package com.example.carsharingonline.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingonline.dto.CreatePaymentRequestDto;
import com.example.carsharingonline.dto.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.PaymentResponseDto;
import com.example.carsharingonline.dto.PaymentStatusResponseDto;
import com.example.carsharingonline.service.PaymentService;
import com.example.carsharingonline.utils.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @WithUserDetails("User@gmail.com")
    @Test
    @DisplayName("Get payments by user ID as USER")
    void getAll_UserAuthority_ReturnsPaymentList() throws Exception {
        // Given
        Long userId = TestDataUtil.TEST_USER_ID;
        PaymentDetailedResponseDto expectedDto = TestDataUtil.getTestPaymentDetailedResponseDto();
        List<PaymentDetailedResponseDto> expected = List.of(expectedDto);
        when(paymentService.getAll(any(), eq(userId))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        get("/registered/payments/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentDetailedResponseDto[] actualArray = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), PaymentDetailedResponseDto[].class);
        assertNotNull(actualArray);
        assertEquals(1, actualArray.length);
        PaymentDetailedResponseDto actual = actualArray[0];
        assertEquals(expectedDto.id(), actual.id());
        assertEquals(expectedDto.status(), actual.status());
        assertEquals(expectedDto.sessionId(), actual.sessionId());
        assertEquals(expectedDto.sessionUrl(), actual.sessionUrl());
        assertEquals(expectedDto.rentalId(), actual.rentalId());
        assertEquals(expectedDto.type(), actual.type());
        assertEquals(0, expectedDto.amountToPay().compareTo(actual.amountToPay()));
    }

    @WithUserDetails("Admin@gmail.com")
    @Test
    @DisplayName("Get payments by user ID as ADMIN")
    void getAll_AdminAuthority_ReturnsPaymentList() throws Exception {
        // Given
        Long userId = TestDataUtil.TEST_USER_ID;
        PaymentDetailedResponseDto expectedDto = TestDataUtil.getTestPaymentDetailedResponseDto();
        List<PaymentDetailedResponseDto> expected = List.of(expectedDto);
        when(paymentService.getAll(any(), eq(userId))).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        get("/registered/payments/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentDetailedResponseDto[] actualArray = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), PaymentDetailedResponseDto[].class);
        assertNotNull(actualArray);
        assertEquals(1, actualArray.length);
        PaymentDetailedResponseDto actual = actualArray[0];
        assertEquals(expectedDto.id(), actual.id());
        assertEquals(expectedDto.status(), actual.status());
        assertEquals(expectedDto.sessionId(), actual.sessionId());
        assertEquals(expectedDto.sessionUrl(), actual.sessionUrl());
        assertEquals(expectedDto.rentalId(), actual.rentalId());
        assertEquals(expectedDto.type(), actual.type());
        assertEquals(0, expectedDto.amountToPay().compareTo(actual.amountToPay()));
    }

    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    @Test
    @DisplayName("Create payment session")
    void createPaymentSession_ValidRequest_Success() throws Exception {
        // Given
        CreatePaymentRequestDto requestDto = TestDataUtil.getTestCreatePaymentRequestDto();
        PaymentResponseDto expected = TestDataUtil.getTestPaymentResponseDto();
        when(paymentService.createPaymentSession(requestDto)).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        post("/registered/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), PaymentResponseDto.class);
        assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"MANAGER"})
    @Test
    @DisplayName("Handle successful payment")
    void handleSuccess_ValidSessionId_Success() throws Exception {
        // Given
        String sessionId = TestDataUtil.TEST_SESSION_ID;
        PaymentStatusResponseDto expected = TestDataUtil.getTestPaymentStatusResponseDto();
        when(paymentService.handleSuccess(sessionId)).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        get("/registered/payments/success")
                                .param("sessionId", sessionId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentStatusResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), PaymentStatusResponseDto.class);
        assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "user", authorities = {"CUSTOMER"})
    @Test
    @DisplayName("Handle canceled payment")
    void handleCancel_ValidSessionId_Success() throws Exception {
        // Given
        String sessionId = TestDataUtil.TEST_SESSION_ID;
        PaymentStatusResponseDto expected = TestDataUtil.getTestPaymentStatusResponseDto();
        when(paymentService.handleCancel(sessionId)).thenReturn(expected);

        // When
        MvcResult result = mockMvc.perform(
                        get("/registered/payments/cancel")
                                .param("sessionId", sessionId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        PaymentStatusResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), PaymentStatusResponseDto.class);
        assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
