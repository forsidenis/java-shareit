package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ru.practicum.shareit.user.UserRepository userRepository;

    @Test
    public void findByRequestorIdOrderByCreatedDesc_ShouldReturnRequests() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        ItemRequest request = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a drill")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(request.getId(), result.get(0).getId());
    }

    @Test
    public void findAllByRequestorIdNot_ShouldReturnOtherUsersRequests() {
        User user1 = userRepository.save(User.builder()
                .name("User 1")
                .email("user1@example.com")
                .build());

        User user2 = userRepository.save(User.builder()
                .name("User 2")
                .email("user2@example.com")
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Request from user 1")
                .requestor(user1)
                .created(LocalDateTime.now())
                .build());

        itemRequestRepository.save(ItemRequest.builder()
                .description("Request from user 2")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build());

        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> result = itemRequestRepository.findAllByRequestorIdNot(user1.getId(), pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(user2.getId(), result.get(0).getRequestor().getId());
    }
}
