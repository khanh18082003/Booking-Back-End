package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.mapper.BookingMapper;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.accommodation.AccommodationService;
import com.booking.bookingbackend.service.price.PriceService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.stereotype.Service;

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
  AccommodationService accommodationService;
  PriceService priceService;
  private final AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer;

  @Override
  public BookingResponse book(BookingRequest request) {
    // Validate request
    validateRequest(request);

    // Check if the properties exist and available accommodations
    final var aAccommodations = request.accommodations().stream()
        .map(accommodation -> {
              var availabilities = accommodationService.checkAvailabilityForBooking(
                  accommodation.id(),
                  request.checkIn(),
                  request.checkOut(),
                  accommodation.quantity()
              );
              return AccommodationBookingResponse.builder()
                  .availableAccommodations(availabilities)
                  .quantity(accommodation.quantity())
                  .build();
            }
        )
        .toList();

    // Calculate the total price
    final BigDecimal totalPrice = priceService.calculatePrice(aAccommodations);

    Booking booking = mapper.toEntity(request);
    booking.setTotalPrice(totalPrice);
    booking.setProperties(propertiesRepository.findById(request.propertiesID())
        .orElseThrow(() -> new AppException(
            ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            Properties.class.getSimpleName()
        ))
    );
    booking.setUser(userRepository.findById(request.userId())
        .orElseThrow(() -> new AppException(
            ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            User.class.getSimpleName()
        ))
    );
    if (request.guestBookingID() != null || booking.getUser() == null) {
      booking.setGuestBooking(guestBookingRepository
          .findById(
              Objects.requireNonNull(request.guestBookingID())
          )
          .orElseThrow(() -> new AppException(
              ErrorCode.MESSAGE_INVALID_ENTITY_ID,
              GuestBooking.class.getSimpleName()
          ))
      );
    }
    booking.setStatus(BookingStatus.Pending);

    // Save the available accommodations
    accommodationService.saveAll(aAccommodations);
    // Save the booking
    Booking savedBooking = repository.save(booking);
    return mapper.toDtoResponse(savedBooking);
  }

  private void validateRequest(BookingRequest request) {
    final var checkInDate = request.checkIn();
    final var checkOutDate = request.checkOut();
    final var currentDate = LocalDate.now();

    if (checkInDate.isAfter(checkOutDate) || checkInDate.isBefore(currentDate)) {
      throw new AppException(ErrorCode.MESSAGE_INVALID_CHECKIN_DATE);
    }
    if (request.adults() <= 0 || request.children() < 0) {
      throw new AppException(ErrorCode.MESSAGE_INVALID_GUESTS);
    }
  }

  @Override
  public BookingResponse changeStatus(UUID id, BookingStatus status) {
    Booking booking = repository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName()));
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
