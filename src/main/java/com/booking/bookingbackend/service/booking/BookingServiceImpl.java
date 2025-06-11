package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.constant.ErrorCode;
import com.booking.bookingbackend.constant.PaymentMethod;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.request.PaymentRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.dto.response.Meta;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.dto.response.PaymentResponse;
import com.booking.bookingbackend.data.dto.response.PropertiesBookingResponse;
import com.booking.bookingbackend.data.dto.response.UserBookingResponse;
import com.booking.bookingbackend.data.entity.Available;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.entity.BookingDetail;
import com.booking.bookingbackend.data.entity.CustomUserDetails;
import com.booking.bookingbackend.data.entity.GuestBooking;
import com.booking.bookingbackend.data.entity.Properties;
import com.booking.bookingbackend.data.entity.User;
import com.booking.bookingbackend.data.entity.ids.BookingDetailId;
import com.booking.bookingbackend.data.mapper.BookingMapper;
import com.booking.bookingbackend.data.projection.BookingDetailResponse;
import com.booking.bookingbackend.data.projection.UserBookingsHistoryDTO;
import com.booking.bookingbackend.data.repository.AvailableRepository;
import com.booking.bookingbackend.data.repository.BookingDetailsRepository;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.data.repository.GuestBookingRepository;
import com.booking.bookingbackend.data.repository.PropertiesRepository;
import com.booking.bookingbackend.data.repository.UserRepository;
import com.booking.bookingbackend.exception.AppException;
import com.booking.bookingbackend.service.accommodation.AccommodationService;
import com.booking.bookingbackend.service.notification.MailService;
import com.booking.bookingbackend.service.payment.PaymentService;
import com.booking.bookingbackend.service.price.PriceService;
import com.booking.bookingbackend.util.SecurityUtils;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
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
  AvailableRepository availableRepository;
  BookingMapper mapper;
  AccommodationService accommodationService;
  PriceService priceService;
  BookingValidationService bookingValidationService;
  PaymentService paymentService;
  MailService mailService;

  private final AbstractScriptDatabaseInitializer abstractScriptDatabaseInitializer;

  @Transactional
  @Override
  public BookingResponse book(BookingRequest request)
      throws MessagingException, UnsupportedEncodingException {
    // Validate request
    bookingValidationService.validateRequest(
        request.checkIn(),
        request.checkOut(),
        request.adults(),
        request.children()
    );

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
    }
    GuestBooking guest = GuestBooking.builder()
        .email(request.guest().email())
        .firstName(request.guest().firstName())
        .lastName(request.guest().lastName())
        .phoneNumber(request.guest().phoneNumber())
        .country(request.guest().country())
        .build();
    guestBookingRepository.save(guest);
    booking.setGuestBooking(guest);

    booking.setStatus(BookingStatus.CONFIRMED);

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

    // Create a payment if the payment method is cash
    PaymentMethod paymentMethod = PaymentMethod.valueOf(request.paymentMethod());

    PaymentRequest paymentRequest = PaymentRequest.builder()
        .amount(totalPrice)
        .paymentMethod(paymentMethod)
        .bookingId(savedBooking.getId())
        .build();
    PaymentResponse paymentResponse = paymentService.save(paymentRequest);

    // Map the saved booking to response DTO
    BookingResponse response = mapper.toDtoResponse(savedBooking);

    response.setUserBooking(UserBookingResponse.builder()
        .email(guest.getEmail())
        .firstName(guest.getFirstName())
        .lastName(guest.getLastName())
        .phone(guest.getPhoneNumber())
        .country(guest.getCountry())
        .build());

    Properties properties = savedBooking.getProperties();
    response.setProperties(PropertiesBookingResponse.builder()
        .id(properties.getId())
        .name(properties.getName())
        .image(properties.getImage())
        .description(properties.getDescription())
        .address(properties.getAddress())
        .ward(properties.getWard())
        .district(properties.getDistrict())
        .city(properties.getCity())
        .province(properties.getProvince())
        .country(properties.getCountry())
        .rating(properties.getRating())
        .totalRating(properties.getTotalRating())
        .checkInTime(properties.getCheckInTime())
        .checkOutTime(properties.getCheckOutTime())
        .propertiesType(properties.getPropertyType().getName())
        .build());
    response.setAccommodations(aAccommodations);
    response.setPayment(paymentResponse);

    mailService.sendMailBookingConfirmation(response);
    return response;
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

  @PreAuthorize("hasRole('USER')")
  @Override
  public PaginationResponse<UserBookingsHistoryDTO> getUserBookingsHistory(
      int pageNo,
      int pageSize
  ) {
    CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
    User user = userDetails.user();

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
    Page<UserBookingsHistoryDTO> page = repository.findUserBookingsHistory(
        user.getId(),
        pageable
    );
    return PaginationResponse.<UserBookingsHistoryDTO>builder()
        .meta(Meta.builder()
            .page(page.getNumber() + 1)
            .pageSize(page.getSize())
            .pages(page.getTotalPages())
            .total(page.getTotalElements())
            .build())
        .data(page.getContent())
        .build();
  }

  @Transactional
  @Override
  public void cancelBooking(UUID bookingId) {
    Booking booking = repository.findById(bookingId)
        .orElseThrow(() -> new AppException(ErrorCode.MESSAGE_INVALID_ENTITY_ID,
            getEntityClass().getSimpleName())
        );
    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new AppException(ErrorCode.MESSAGE_BOOKING_ALREADY_CANCELLED);
    }

    booking.setStatus(BookingStatus.CANCELLED);
    booking = repository.save(booking);
    LocalDate checkIn = booking.getCheckIn();
    LocalDate checkOut = booking.getCheckOut();

    List<BookingDetail> bookingDetailList = bookingDetailRepository.findAllByBookingId(
        booking.getId());

    bookingDetailList.forEach(bookingDetail -> {
      List<Available> availables = availableRepository.findAllByAccommodationIdBetweenCheckInAndCheckOut(
          bookingDetail.getId().getAccommodationId(),
          checkIn,
          checkOut.minusDays(1)
      );
      if (!availables.isEmpty()) {
        availables.forEach(available -> {
          available.setTotalReserved(available.getTotalReserved() - bookingDetail.getBookedUnits());
        });
        availableRepository.saveAll(availables);
      }
    });

  }

  @Override
  public PaginationResponse<BookingDetailResponse> getAllBookingsByPropertiesId(
      String id,
      int pageNo,
      int pageSize
  ) {
    Pageable pageable = PageRequest.of(
        pageNo - 1,
        pageSize,
        Sort.by(Direction.DESC, "createdAt")
    );

    if (id.equals("all")) {
      CustomUserDetails userDetails = SecurityUtils.getCurrentUser();
      Page<BookingDetailResponse> bookingPage = repository.findAllOfHost(userDetails.user().getId(), pageable);

      return PaginationResponse.<BookingDetailResponse>builder()
          .meta(Meta.builder()
              .page(bookingPage.getNumber() + 1)
              .pageSize(bookingPage.getSize())
              .pages(bookingPage.getTotalPages())
              .total(bookingPage.getTotalElements())
              .build())
          .data(bookingPage.getContent())
          .build();
    } else {
      Properties properties = propertiesRepository.findById(UUID.fromString(id))
          .orElseThrow(() -> new AppException(
              ErrorCode.MESSAGE_INVALID_ENTITY_ID,
              Properties.class.getSimpleName()
          ));
      Page<BookingDetailResponse> bookingPage = repository.findAllByPropertiesId(
          properties.getId(),
          pageable
      );

      return PaginationResponse.<BookingDetailResponse>builder()
          .meta(Meta.builder()
              .page(bookingPage.getNumber() + 1)
              .pageSize(bookingPage.getSize())
              .pages(bookingPage.getTotalPages())
              .total(bookingPage.getTotalElements())
              .build())
          .data(bookingPage.getContent())
          .build();
    }
  }


}
