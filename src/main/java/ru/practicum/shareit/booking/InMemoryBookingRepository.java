package ru.practicum.shareit.booking;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idCounter++);
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBookerId() != null &&
                        booking.getBookerId().equals(bookerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemId(Long itemId) {
        return bookings.values().stream()
                .filter(booking -> booking.getItemId() != null &&
                        booking.getItemId().equals(itemId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        // В реальной реализации здесь должна быть логика поиска по владельцу через itemId
        // Временно возвращаем все бронирования
        return new ArrayList<>(bookings.values());
    }

    @Override
    public void deleteById(Long id) {
        bookings.remove(id);
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }
}
