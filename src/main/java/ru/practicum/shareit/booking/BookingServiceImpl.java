package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User booker = getUserById(userId);
        Item item = getItemById(bookingRequestDto.getItemId());

        validateBooking(bookingRequestDto, item, booker);

        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Created booking with id: {} for user: {}", savedBooking.getId(), userId);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new SecurityException("User " + userId + " is not the owner of item " + booking.getItem().getId());
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status is already: " + booking.getStatus());
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Updated booking {} status to: {}", bookingId, updatedBooking.getStatus());
        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User " + userId + " does not have access to booking " + bookingId);
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookings(String state, Long userId, int from, int size) {
        getUserById(userId);

        BookingState bookingState = parseState(state);
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(String state, Long userId, int from, int size) {
        getUserById(userId);

        BookingState bookingState = parseState(state);
        Pageable pageable = createPageable(from, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (bookingState) {
            case CURRENT:
                bookings = bookingRepository.findCurrentByOwnerId(userId, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findPastByOwnerId(userId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findFutureByOwnerId(userId, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByOwnerId(userId, pageable);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void validateBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Owner cannot book their own item");
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
            throw new ValidationException("End date must be after start date");
        }

        if (bookingRequestDto.getEnd().equals(bookingRequestDto.getStart())) {
            throw new ValidationException("Start and end dates cannot be equal");
        }

        List<Booking> overlappingBookings = bookingRepository.findByItemIdAndStatusNot(
                item.getId(), BookingStatus.REJECTED);

        for (Booking existingBooking : overlappingBookings) {
            if (isOverlapping(bookingRequestDto, existingBooking)) {
                throw new ValidationException("Booking overlaps with existing booking");
            }
        }
    }

    private boolean isOverlapping(BookingRequestDto newBooking, Booking existingBooking) {
        return !(newBooking.getEnd().isBefore(existingBooking.getStart()) ||
                newBooking.getStart().isAfter(existingBooking.getEnd()));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }

    private Pageable createPageable(int from, int size, Sort sort) {
        if (from < 0) {
            throw new ValidationException("From parameter must be non-negative");
        }
        if (size <= 0) {
            throw new ValidationException("Size parameter must be positive");
        }
        return PageRequest.of(from / size, size, sort);
    }
}
