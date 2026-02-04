package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ItemCommentDataJpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void addComment_ValidBooking_AddsCommentSuccessfully() {
        // Создаем владельца и бронировщика
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        User booker = userRepository.save(User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build());

        // Создаем вещь
        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        // Создаем завершенное бронирование
        Booking booking = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build());

        // Создаем комментарий
        Comment comment = commentRepository.save(Comment.builder()
                .text("Great drill, worked perfectly!")
                .item(item)
                .author(booker)
                .created(LocalDateTime.now())
                .build());

        assertNotNull(comment);
        assertEquals("Great drill, worked perfectly!", comment.getText());
        assertEquals("Booker", comment.getAuthor().getName());
    }

    @Test
    public void testFindByItemId() {
        User owner = userRepository.save(User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .owner(owner)
                .build());

        User commenter = userRepository.save(User.builder()
                .name("Commenter")
                .email("commenter@example.com")
                .build());

        commentRepository.save(Comment.builder()
                .text("Great drill!")
                .item(item)
                .author(commenter)
                .created(LocalDateTime.now())
                .build());

        var comments = commentRepository.findByItemId(item.getId());
        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals("Great drill!", comments.get(0).getText());
    }
}
