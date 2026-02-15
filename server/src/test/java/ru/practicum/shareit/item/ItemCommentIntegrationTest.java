package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemCommentIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Создаем владельца
        User owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        User savedOwner = userRepository.save(owner);
        ownerId = savedOwner.getId();

        // Создаем бронировщика
        User booker = User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        User savedBooker = userRepository.save(booker);
        bookerId = savedBooker.getId();
    }

    @Test
    public void addComment_ValidBooking_AddsCommentSuccessfully() {
        // Создаем item
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        var createdItem = itemService.createItem(itemDto, ownerId);
        itemId = createdItem.getId();
        assertEquals(ownerId, createdItem.getOwnerId());

        // Создаем бронирование
        ru.practicum.shareit.booking.dto.BookingRequestDto bookingRequestDto =
                ru.practicum.shareit.booking.dto.BookingRequestDto.builder()
                        .itemId(itemId)
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .build();

        var booking = bookingService.createBooking(bookingRequestDto, bookerId);

        // Подтверждаем бронирование
        bookingService.approveBooking(booking.getId(), true, ownerId);

        // Имитируем завершение бронирования (изменяем даты в БД)
        var bookingEntity = bookingRepository.findById(booking.getId()).orElseThrow();
        bookingEntity.setStart(LocalDateTime.now().minusDays(2));
        bookingEntity.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(bookingEntity);

        // Добавляем комментарий
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("Great drill, worked perfectly!")
                .build();

        var comment = itemService.addComment(itemId, commentRequestDto, bookerId);

        assertNotNull(comment);
        assertEquals("Great drill, worked perfectly!", comment.getText());
        assertEquals("Booker", comment.getAuthorName());
    }
}
