package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        log.info("Creating booking for user: {}", userId);
        return bookingDto;
    }

    @Override
    public BookingDto approveBooking(Long bookingId, boolean approved, Long userId) {
        log.info("Approving booking {} for user: {}", bookingId, userId);
        return new BookingDto();
    }

    @Override
    public BookingDto getBooking(Long bookingId, Long userId) {
        log.info("Getting booking {} for user: {}", bookingId, userId);
        return new BookingDto();
    }

    @Override
    public List<BookingDto> getAllBookings(String state, Long userId, int from, int size) {
        log.info("Getting all bookings for user: {} with state: {}", userId, state);
        return Collections.emptyList();
    }

    @Override
    public List<BookingDto> getOwnerBookings(String state, Long userId, int from, int size) {
        log.info("Getting owner bookings for user: {} with state: {}", userId, state);
        return Collections.emptyList();
    }
}
