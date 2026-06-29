package com.tripplanner.backend.service;

import com.tripplanner.backend.domain.Booking;
import com.tripplanner.backend.domain.BookingStatus;
import com.tripplanner.backend.dto.booking.*;
import com.tripplanner.backend.exception.ForbiddenException;
import com.tripplanner.backend.exception.NotFoundException;
import com.tripplanner.backend.exception.ValidationException;
import com.tripplanner.backend.mock.domain.Place;
import com.tripplanner.backend.mock.domain.PlaceType;
import com.tripplanner.backend.mock.repository.PlaceRepository;
import com.tripplanner.backend.mock.repository.PlaceTypeRepository;
import com.tripplanner.backend.mock.security.SecurityUtil;
import com.tripplanner.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PlaceRepository placeRepository;
    private final PlaceTypeRepository placeTypeRepository;

    @Transactional(readOnly = true)
    public Page<BookingResponse> listBookings(BookingStatus status, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        boolean filterByUserId = userId != null;
        boolean filterByStatus = status != null;

        if (filterByUserId && !SecurityUtil.isAdmin()) {
            throw new ForbiddenException("Only admins can filter by userId");
        }

        Long effectiveUserId = SecurityUtil.isAdmin() ? userId : SecurityUtil.getCurrentUserId();

        Page<Booking> bookings;
        if (effectiveUserId != null && filterByStatus) {
            bookings = bookingRepository.findByUserIdAndStatus(effectiveUserId, status, pageable);
        } else if (effectiveUserId != null) {
            bookings = bookingRepository.findByUserId(effectiveUserId, pageable);
        } else if (filterByStatus) {
            bookings = bookingRepository.findByStatus(status, pageable);
        } else {
            bookings = bookingRepository.findAll(pageable);
        }

        return bookings.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id) {
        Booking booking = findBookingOrThrow(id);
        checkAccess(booking);
        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        Place place = findHotelPlaceOrThrow(request.getPlaceId());
        validateDates(request.getCheckIn(), request.getCheckOut());

        Booking booking = Booking.builder()
                .userId(SecurityUtil.getCurrentUserId())
                .placeId(place.getId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .status(BookingStatus.PENDING)
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateBooking(Long id, UpdateBookingRequest request) {
        Booking booking = findBookingOrThrow(id);

        if (!booking.getUserId().equals(SecurityUtil.getCurrentUserId())) {
            throw new ForbiddenException("Not owner of booking");
        }

        Place place = findHotelPlaceOrThrow(request.getPlaceId());
        validateDates(request.getCheckIn(), request.getCheckOut());

        booking.setPlaceId(place.getId());
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());

        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateStatus(Long id, UpdateBookingStatusRequest request) {
        Booking booking = findBookingOrThrow(id);
        booking.setStatus(request.getStatus());
        return mapToResponse(bookingRepository.save(booking));
    }

    private Booking findBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private Place findHotelPlaceOrThrow(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Place not found"));

        PlaceType placeType = placeTypeRepository.findById(place.getPlaceTypeId())
                .orElseThrow(() -> new NotFoundException("Place type not found"));

        if (!"HOTEL".equalsIgnoreCase(placeType.getCode())) {
            throw new ValidationException("Place is not a hotel");
        }

        return place;
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("checkOut must be after checkIn");
        }
    }

    private void checkAccess(Booking booking) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!booking.getUserId().equals(currentUserId) && !SecurityUtil.isAdmin()) {
            throw new ForbiddenException("Not booking owner nor admin");
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .placeId(booking.getPlaceId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
