package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(1L)
                .build();
    }

    @Test
    public void updateItem_ValidUpdate_ReturnsUpdatedItemDto() {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.update(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.updateItem(1L, updateDto, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository, times(1)).update(any(Item.class));
    }

    @Test
    public void updateItem_WrongOwner_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(1L, itemDto, 2L));
    }

    @Test
    public void getItemById_ItemExists_ReturnsItemDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemResponseDto result = itemService.getItemById(1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    public void getAllItemsByOwner_ReturnsOwnerItems() {
        when(itemRepository.findByOwner(1L)).thenReturn(List.of(item));

        List<ItemResponseDto> result = itemService.getAllItems(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void searchItems_WithMatchingText_ReturnsItems() {
        when(itemRepository.search("test")).thenReturn(List.of(item));

        List<ItemResponseDto> result = itemService.searchItems("test", 1L);

        assertFalse(result.isEmpty());
    }

    @Test
    public void searchItems_EmptyText_ReturnsEmptyList() {
        List<ItemResponseDto> result = itemService.searchItems("", 1L);

        assertTrue(result.isEmpty());
    }
}
