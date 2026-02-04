package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, boolean approved, Long userId);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getAllBookings(String state, Long userId, int from, int size);

    List<BookingResponseDto> getOwnerBookings(String state, Long userId, int from, int size);
}
