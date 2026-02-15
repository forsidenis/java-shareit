package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void createBooking_ValidBooking_ReturnsBookingDto() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        var item = itemService.createItem(itemDto, owner.getId());

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        var result = bookingService.createBooking(bookingRequestDto, booker.getId());

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    public void getBooking_ValidRequest_ReturnsBookingDto() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        var item = itemService.createItem(itemDto, owner.getId());

        BookingRequestDto bookingRequestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        var createdBooking = bookingService.createBooking(bookingRequestDto, booker.getId());

        var result = bookingService.getBooking(createdBooking.getId(), booker.getId());

        assertNotNull(result);
        assertEquals(createdBooking.getId(), result.getId());
    }
}
