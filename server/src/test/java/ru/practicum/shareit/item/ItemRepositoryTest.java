package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchAvailableItems_ValidText_ReturnsItems() {
        User owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        entityManager.persist(owner);

        Item item1 = Item.builder()
                .name("Power Drill")
                .description("Electric power drill")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);

        Item item2 = Item.builder()
                .name("Hammer")
                .description("Heavy hammer")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item2);

        entityManager.flush();

        List<Item> result = itemRepository.searchAvailableItems(
                "drill",
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Power Drill", result.get(0).getName());
    }

    @Test
    void findByOwnerId_ValidOwner_ReturnsItems() {
    }
}
