package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestBody BookingRequestDto bookingRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.getOwnerBookings(state, userId, from, size);
    }
}
