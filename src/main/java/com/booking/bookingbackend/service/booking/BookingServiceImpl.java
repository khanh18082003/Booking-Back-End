package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.mapper.BookingMapper;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "BOOKING-SERVICE")
public class BookingServiceImpl implements BookingService {

    BookingRepository repository;
    PropertiesRepository propertiesRepository;
    UserRepository userRepository;
    GuestBookingRepository guestBookingRepository;
    BookingMapper mapper;

    @Override
    public BookingResponse save(BookingRequest request) {
        Booking booking = mapper.toEntity(request);
        booking.setProperties(propertiesRepository.findById(request.propertiesID())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        booking.setUser(userRepository.findById(request.userId())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        booking.setGuestBooking(guestBookingRepository.findById(request.guestBookingID())
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName())));
        Booking savedBooking = repository.save(booking);
        return mapper.toDtoResponse(savedBooking);
    }

    @Override
    public BookingResponse changeStatus(UUID id, BookingStatus status) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID , getEntityClass().getSimpleName()));
        booking.setStatus(status);
        Booking updatedBooking = repository.save(booking);
        return mapper.toDtoResponse(updatedBooking);
    }

    @Override
    public List<BookingResponse> BookingHistory(UUID userId) {
        List<Booking> bookingList = repository.findAllByUserId(userId);
        return bookingList.stream()
                .map(mapper::toDtoResponse)
                .collect(Collectors.toList());
    }
}
