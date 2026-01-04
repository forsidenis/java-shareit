package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto, Long userId);

    BookingDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getAllBookings(String state, Long userId, int from, int size);

    List<BookingDto> getOwnerBookings(String state, Long userId, int from, int size);
}
