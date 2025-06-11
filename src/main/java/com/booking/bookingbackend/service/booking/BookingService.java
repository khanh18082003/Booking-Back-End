package com.booking.bookingbackend.service.booking;

import com.booking.bookingbackend.constant.BookingStatus;
import com.booking.bookingbackend.data.dto.request.BookingRequest;
import com.booking.bookingbackend.data.dto.response.BookingResponse;
import com.booking.bookingbackend.data.dto.response.PaginationResponse;
import com.booking.bookingbackend.data.entity.Booking;
import com.booking.bookingbackend.data.projection.BookingDetailResponse;
import com.booking.bookingbackend.data.projection.UserBookingsHistoryDTO;
import com.booking.bookingbackend.data.repository.BookingRepository;
import com.booking.bookingbackend.service.BaseEntityService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public interface BookingService extends
    BaseEntityService<UUID, Booking, BookingRepository, BookingResponse> {

  @Override
  default Class<?> getEntityClass() {
    return Booking.class;
  }

  BookingResponse book(BookingRequest bookingRequest)
      throws MessagingException, UnsupportedEncodingException;

  BookingResponse changeStatus(UUID id, BookingStatus status);

  List<BookingResponse> BookingHistory(UUID userId);

  PaginationResponse<UserBookingsHistoryDTO> getUserBookingsHistory(
      int pageNo,
      int pageSize
  );

  void cancelBooking(UUID bookingId);

  PaginationResponse<BookingDetailResponse> getAllBookingsByPropertiesId(
      String id,
      int pageNo,
      int pageSize
  );
}
