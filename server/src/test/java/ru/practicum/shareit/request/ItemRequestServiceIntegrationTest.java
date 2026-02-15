package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createRequest_ValidRequest_ReturnsRequestDto() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill for home renovation")
                .build();

        var result = itemRequestService.createRequest(requestDto, user.getId());

        assertNotNull(result);
        assertEquals("Need a drill for home renovation", result.getDescription());
        assertEquals(user.getId(), result.getRequestorId());
    }

    @Test
    public void getUserRequests_ValidUser_ReturnsRequests() {
        User user = userRepository.save(User.builder()
                .name("Test User")
                .email("test@example.com")
                .build());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a hammer")
                .build();
        itemRequestService.createRequest(requestDto, user.getId());

        var result = itemRequestService.getUserRequests(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Need a hammer", result.get(0).getDescription());
    }
}
