package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.dto.response.PaymentBookingResponse;
import com.booking.bookingbackend.data.dto.response.UserBookingResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.entity.BookingDetail;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.entity.Profile;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.entity.ids.BookingDetailId;
import com.booking.bookingbackend.data.mapper.BookingMapper;
import com.booking.bookingbackend.data.repository.BookingDetailsRepository;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.accommodation.AccommodationService;
import com.booking.bookingbackend.service.price.PriceService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
  BookingDetailsRepository bookingDetailRepository;
  BookingMapper mapper;
  AccommodationService accommodationService;
  PriceService priceService;
  private final AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer;

  @Transactional
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
                  .id(accommodation.id())
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
    if (request.userId() != null) {
      booking.setUser(userRepository.findById(request.userId())
          .orElseThrow(() -> new AppException(
              ErrorCode.MESSAGE_INVALID_ENTITY_ID,
              User.class.getSimpleName()
          ))
      );
    } else {
      GuestBooking guest = GuestBooking.builder()
          .email(request.guest().email())
          .firstName(request.guest().firstName())
          .lastName(request.guest().lastName())
          .phoneNumber(request.guest().phoneNumber())
          .country(request.guest().country())
          .build();
      guestBookingRepository.save(guest);
      booking.setGuestBooking(guest);
    }

    booking.setStatus(BookingStatus.Pending);

    // Save the available accommodations
    accommodationService.saveAll(aAccommodations);
    // Save the booking
    Booking savedBooking = repository.save(booking);

    // Save the booking details
    List<BookingDetail> bookingDetails = aAccommodations.stream()
        .map(accommodation -> {
              BigDecimal price = accommodation.getAvailableAccommodations().stream()
                  .reduce(
                      BigDecimal.ZERO,
                      (acc, availableAccommodation) -> acc.add(availableAccommodation.price()),
                      BigDecimal::add
                  );
              return BookingDetail.builder()
                  .id(BookingDetailId.builder()
                      .bookingId(savedBooking.getId())
                      .accommodationId(accommodation.getId())
                      .build())
                  .bookedUnits(accommodation.getQuantity())
                  .totalNights((int) ChronoUnit.DAYS.between(request.checkIn(), request.checkOut()))
                  .totalPrice(price)
                  .build();
            }
        ).toList();
    bookingDetailRepository.saveAll(bookingDetails);
    BookingResponse response = mapper.toDtoResponse(savedBooking);
    if (savedBooking.getUser() != null) {
      Profile profile = savedBooking.getUser().getProfile();
      response.setUserBooking(UserBookingResponse.builder()
          .email(savedBooking.getUser().getEmail())
          .firstName(profile.getFirstName())
          .lastName(profile.getLastName())
          .phone(profile.getPhone())
          .country(profile.getNationality())
          .build());
    } else {
      GuestBooking guest = savedBooking.getGuestBooking();
      response.setUserBooking(UserBookingResponse.builder()
          .email(guest.getEmail())
          .firstName(guest.getFirstName())
          .lastName(guest.getLastName())
          .phone(guest.getPhoneNumber())
          .country(guest.getCountry())
          .build());
    }
    response.setPropertiesId(savedBooking.getProperties().getId());
    response.setAccommodations(aAccommodations);
    response.setPayment(PaymentBookingResponse.builder()
        .status(false)
        .paymentMethod(PaymentMethod.valueOf(request.paymentMethod()))
        .build());
    return response;
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
