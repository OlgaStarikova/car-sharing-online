package com.example.carsharingonline.mapper;

import com.example.carsharingonline.config.MapperConfig;
import com.example.carsharingonline.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingonline.dto.payment.PaymentResponseDto;
import com.example.carsharingonline.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingonline.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {

    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    PaymentDetailedResponseDto toDetailedDto(Payment payment);

    PaymentStatusResponseDto toStatusDto(Payment payment);
}
