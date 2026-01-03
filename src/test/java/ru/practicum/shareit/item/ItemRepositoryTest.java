package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ItemRepositoryTest {

    private ItemRepository itemRepository;
    private User owner;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        itemRepository = new ItemRepository();
        owner = new User(1L, "Owner", "owner@example.com");
        item1 = new Item(null, "Drill", "Powerful drill", true, owner, null);
        item2 = new Item(null, "Hammer", "Heavy hammer", false, owner, null);
    }

    @Test
    public void save_newItem_assignsId() {
        Item saved = itemRepository.save(item1);

        assertNotNull(saved.getId());
        assertEquals("Drill", saved.getName());
    }

    @Test
    public void findById_exists_returnsItem() {
        Item saved = itemRepository.save(item1);

        Item found = itemRepository.findById(saved.getId()); // Убрали Optional<>

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    public void findById_notExists_returnsNull() {
        Item found = itemRepository.findById(999L); // Убрали Optional<>

        assertNull(found);
    }

    @Test
    public void findByOwnerId_multipleItems_returnsOwnerItems() {
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findByOwnerId(1L);

        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(item -> item.getOwner().getId().equals(1L)));
    }

    @Test
    public void search_matchingText_returnsAvailableItems() {
        itemRepository.save(item1); // available = true
        itemRepository.save(item2); // available = false

        List<Item> results = itemRepository.search("drill");

        assertEquals(1, results.size());
        assertEquals("Drill", results.get(0).getName());
        assertTrue(results.get(0).getAvailable());
    }

    @Test
    public void search_matchingInDescription_returnsItem() {
        Item item = new Item(null, "Tool", "This is a powerful drill", true, owner, null);
        itemRepository.save(item);

        List<Item> results = itemRepository.search("drill");

        assertEquals(1, results.size());
        assertEquals("Tool", results.get(0).getName());
    }

    @Test
    public void search_emptyText_returnsEmptyList() {
        itemRepository.save(item1);

        List<Item> results = itemRepository.search("");

        assertTrue(results.isEmpty());
    }

    @Test
    public void findAll_multipleItems_returnsAll() {
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAll();

        assertEquals(2, items.size());
    }

    @Test
    public void update_exists_updatesItem() {
        Item saved = itemRepository.save(item1);
        saved.setName("Updated Drill");

        Item updated = itemRepository.update(saved);

        assertNotNull(updated);
        assertEquals("Updated Drill", updated.getName());
        assertEquals("Powerful drill", updated.getDescription());
    }

    @Test
    public void update_notExists_returnsNull() {
        Item nonExistingItem = new Item(999L, "Non-existing", "Test", true, owner, null);

        Item updated = itemRepository.update(nonExistingItem);

        assertNull(updated);
    }

    @Test
    public void clear_removesAllItems() {
        itemRepository.save(item1);
        itemRepository.save(item2);

        itemRepository.clear();

        List<Item> items = itemRepository.findAll();
        assertTrue(items.isEmpty());
    }
}
