package com.booking.bookingbackend.service.accommodation;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.booking.bookingbackend.data.dto.request.AccommodationCreationRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationUpdateRequest;
import com.booking.bookingbackend.data.dto.request.AccommodationsSearchRequest;
import com.booking.bookingbackend.data.dto.request.AvailableUpdatePriceRequest;
import com.booking.bookingbackend.data.dto.response.AccommodationBookingResponse;
import com.booking.bookingbackend.data.dto.response.AccommodationResponse;
import com.booking.bookingbackend.data.dto.response.AvailableResponse;
import com.booking.bookingbackend.data.entity.Accommodation;
import com.booking.bookingbackend.data.projection.AccommodationHostDTO;
import com.booking.bookingbackend.data.projection.AccommodationSearchDTO;
import com.booking.bookingbackend.data.projection.AvailableAccommodationDTO;
import com.booking.bookingbackend.data.repository.AccommodationRepository;
import com.booking.bookingbackend.service.BaseEntityService;

public interface AccommodationService
        extends BaseEntityService<UUID, Accommodation, AccommodationRepository, AccommodationResponse> {

    default Class<?> getEntityClass() {
        return Accommodation.class;
    }

    AccommodationResponse save(final AccommodationCreationRequest request);

    void saveAll(List<AccommodationBookingResponse> aDays);

    List<AvailableAccommodationDTO> checkAvailabilityForBooking(
            final UUID id, final LocalDate checkInDate, final LocalDate checkOutDate, final Integer quantity);

    AccommodationResponse update(final UUID id, final AccommodationUpdateRequest request);

    List<AccommodationSearchDTO> findAccommodationByPropertyId(final AccommodationsSearchRequest request);

    List<AccommodationHostDTO> getAccommodationsByPropertyId(final UUID id);

    List<AvailableResponse> findAllByAccommodationId(final UUID id);

    List<AvailableResponse> updatePriceAvailableByDate(final AvailableUpdatePriceRequest request);
}
