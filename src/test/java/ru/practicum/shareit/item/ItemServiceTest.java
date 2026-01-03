package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private ItemServiceImpl itemService;
    private User owner;
    private Item item1;
    private Item item2;
    private ItemDto itemDto1;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository);

        owner = new User(1L, "Owner", "owner@example.com");
        item1 = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        item2 = new Item(2L, "Hammer", "Heavy hammer", true, owner, null);
        itemDto1 = new ItemDto(null, "Drill", "Powerful drill", true, null);
    }

    @Test
    public void createItem_success() {
        when(userRepository.findById(1L)).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item1);

        ItemDto result = itemService.createItem(itemDto1, 1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
        assertEquals("Powerful drill", result.getDescription());
        assertTrue(result.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void createItem_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                itemService.createItem(itemDto1, 99L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void createItem_missingName_throwsException() {
        ItemDto invalidDto = new ItemDto(null, null, "Description", true, null);

        when(userRepository.findById(1L)).thenReturn(owner);

        assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem(invalidDto, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void createItem_missingDescription_throwsException() {
        ItemDto invalidDto = new ItemDto(null, "Name", null, true, null);

        when(userRepository.findById(1L)).thenReturn(owner);

        assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem(invalidDto, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void createItem_missingAvailable_throwsException() {
        ItemDto invalidDto = new ItemDto(null, "Name", "Description", null, null);

        when(userRepository.findById(1L)).thenReturn(owner);

        assertThrows(IllegalArgumentException.class, () ->
                itemService.createItem(invalidDto, 1L));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    public void updateItem_success() {
        ItemDto updateDto = new ItemDto(null, "Drill Pro", "Updated description", null, null);
        Item updatedItem = new Item(1L, "Drill Pro", "Updated description", true, owner, null);

        when(itemRepository.findById(1L)).thenReturn(item1);
        when(userRepository.findById(1L)).thenReturn(owner);
        when(itemRepository.update(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertEquals("Drill Pro", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(itemRepository, times(1)).update(any(Item.class));
    }

    @Test
    public void updateItem_notOwner_throwsException() {
        when(itemRepository.findById(1L)).thenReturn(item1);

        assertThrows(SecurityException.class, () ->
                itemService.updateItem(1L, itemDto1, 999L));
        verify(itemRepository, never()).update(any(Item.class));
    }

    @Test
    public void updateItem_itemNotFound_throwsException() {
        when(itemRepository.findById(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                itemService.updateItem(99L, itemDto1, 1L));
        verify(itemRepository, never()).update(any(Item.class));
    }

    @Test
    public void updateItem_partialUpdate_success() {
        ItemDto updateDto = new ItemDto(null, null, "Updated description", false, null);
        Item updatedItem = new Item(1L, "Drill", "Updated description", false, owner, null);

        when(itemRepository.findById(1L)).thenReturn(item1);
        when(userRepository.findById(1L)).thenReturn(owner);
        when(itemRepository.update(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, updateDto, 1L);

        assertEquals("Drill", result.getName()); // Имя осталось прежним
        assertEquals("Updated description", result.getDescription());
        assertFalse(result.getAvailable()); // Доступность изменилась
    }

    @Test
    public void getItemById_success() {
        when(itemRepository.findById(1L)).thenReturn(item1);

        ItemDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
    }

    @Test
    public void getItemById_notFound_throwsException() {
        when(itemRepository.findById(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> itemService.getItemById(99L));
    }

    @Test
    public void getItemsByOwner_success() {
        when(itemRepository.findByOwnerId(1L)).thenReturn(Arrays.asList(item1, item2));

        List<ItemDto> result = itemService.getItemsByOwner(1L);

        assertEquals(2, result.size());
        assertEquals("Drill", result.get(0).getName());
        assertEquals("Hammer", result.get(1).getName());
    }

    @Test
    public void getItemsByOwner_noItems_returnsEmptyList() {
        when(itemRepository.findByOwnerId(2L)).thenReturn(Arrays.asList());

        List<ItemDto> result = itemService.getItemsByOwner(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void searchItems_success() {
        when(itemRepository.search("drill")).thenReturn(Arrays.asList(item1));

        List<ItemDto> result = itemService.searchItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    public void searchItems_emptyText_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertTrue(result.isEmpty());
    }

    @Test
    public void searchItems_nullText_returnsEmptyList() {
        List<ItemDto> result = itemService.searchItems(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void searchItems_onlyAvailableItems() {
        Item unavailableItem = new Item(3L, "Drill", "Broken drill", false, owner, null);
        when(itemRepository.search("drill")).thenReturn(Arrays.asList(item1)); // Only available item

        List<ItemDto> result = itemService.searchItems("drill");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAvailable());
    }

    @Test
    public void searchItems_caseInsensitive() {
        when(itemRepository.search("DRILL")).thenReturn(Arrays.asList(item1));

        List<ItemDto> result = itemService.searchItems("DRILL");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }
}
