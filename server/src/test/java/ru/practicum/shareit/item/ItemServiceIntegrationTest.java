package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    private Long ownerId;

    @BeforeEach
    public void setUp() {
        // Очистка базы данных перед каждым тестом
        userRepository.deleteAll();

        // Создаем владельца
        User owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        User savedOwner = userRepository.save(owner);
        ownerId = savedOwner.getId();
    }

    @Test
    public void createItem_ValidItem_ReturnsItemDto() {
        // Создаем item
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        var result = itemService.createItem(itemDto, ownerId);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
        assertEquals(ownerId, result.getOwnerId()); // Теперь ownerId доступен
    }
}
