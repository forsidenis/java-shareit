package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class BookingRepositoryIntegrationTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ru.practicum.shareit.user.UserRepository userRepository;

    @Autowired
    private ru.practicum.shareit.item.ItemRepository itemRepository;

    @Test
    public void findByBookerIdOrderByStartDesc_ShouldReturnBookings() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build());

        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findByBookerIdOrderByStartDesc(user.getId(), pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    public void findByItemOwnerIdOrderByStartDesc_ShouldReturnBookings() {
    }
}
