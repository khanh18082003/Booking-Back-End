package com.booking.bookingbackend.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.data.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityDtoMapper<Payment, PaymentResponse> {
    Payment toEntity(PaymentRequest paymentRequest);

    void merge(PaymentRequest paymentRequest, @MappingTarget Payment entity);
}
