package com.booking.bookingbackend.service.guest;

import com.booking.bookingbackend.data.base.EntityDtoMapper;
import com.booking.bookingbackend.data.dto.request.GuestBookingRequest;
import com.booking.bookingbackend.data.dto.response.GuestBookingResponse;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.mapper.GuestBookingMapper;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "GUEST-BOOKING-SERVICE")
public class GuestBookingImpl implements GuestBookingService {
    GuestBookingRepository repository;
    GuestBookingMapper mapper;
    @Override
    public GuestBookingResponse save(GuestBookingRequest guestBookingRequest) {
        GuestBooking guestBooking = mapper.toEntity(guestBookingRequest);
        GuestBooking savedGuestBooking = repository.save(guestBooking);
        return mapper.toDtoResponse(savedGuestBooking);
    }
}
