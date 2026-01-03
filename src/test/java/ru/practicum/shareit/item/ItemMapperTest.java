package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    public void toItemDto_withRequest_returnsDtoWithRequestId() {
        User owner = new User(1L, "Owner", "owner@example.com");
        ItemRequest request = new ItemRequest();
        request.setId(100L);

        Item item = new Item(1L, "Item", "Description", true, owner, request);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("Item", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
        assertEquals(100L, dto.getRequestId());
    }

    @Test
    public void toItemDto_withoutRequest_returnsDtoWithNullRequestId() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        ItemDto dto = ItemMapper.toItemDto(item);

        assertNull(dto.getRequestId());
    }

    @Test
    public void toItem_withDtoAndOwner_returnsItem() {
        ItemDto dto = new ItemDto(null, "Item", "Description", true, null);
        User owner = new User(1L, "Owner", "owner@example.com");

        Item item = ItemMapper.toItem(dto, owner);

        assertNull(item.getId()); // ID should be null for new items
        assertEquals("Item", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
    }
}
