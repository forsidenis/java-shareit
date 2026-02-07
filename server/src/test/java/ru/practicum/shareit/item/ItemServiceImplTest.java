package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ru.practicum.shareit.booking.BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ru.practicum.shareit.request.ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemResponseDto itemResponseDto;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
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

        itemUpdateDto = ItemUpdateDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    public void updateItem_ValidUpdate_ReturnsUpdatedItemDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        // Мокаем методы для получения бронирований
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());

        ItemResponseDto result = itemService.updateItem(1L, itemUpdateDto, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(owner.getId(), result.getOwnerId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void updateItem_WrongOwner_ThrowsNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () ->
                itemService.updateItem(1L, itemUpdateDto, 2L));
    }

    @Test
    public void getItemById_ItemExists_ReturnsItemDto() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());

        ItemResponseDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    public void getAllItemsByOwner_ReturnsOwnerItems() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id"));
        when(itemRepository.findByOwnerId(1L, pageable)).thenReturn(List.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        // Мокаем методы для получения бронирований и комментариев
        when(bookingRepository.findByItemIdInAndEndBeforeAndStatusOrderByEndDesc(anyList(), any(), any()))
                .thenReturn(List.of());
        when(bookingRepository.findByItemIdInAndStartAfterAndStatusOrderByStartAsc(anyList(), any(), any()))
                .thenReturn(List.of());
        when(commentRepository.findByItemIdIn(anyList())).thenReturn(List.of());

        List<ItemResponseDto> result = itemService.getAllItems(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    public void searchItems_WithMatchingText_ReturnsItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        when(itemRepository.searchAvailableItems(eq("test"), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemResponseDto> result = itemService.searchItems("test", 1L);

        assertFalse(result.isEmpty());
    }

    @Test
    public void searchItems_EmptyText_ReturnsEmptyList() {
        // Убираем ненужный мок, так как метод searchItems не вызывает userRepository при пустом тексте
        List<ItemResponseDto> result = itemService.searchItems("", 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void updateItem_EmptyName_DoesNotUpdateName() {
        ItemUpdateDto updateDto = ItemUpdateDto.builder()
                .name("")
                .description("Updated Description")
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of());

        assertDoesNotThrow(() -> itemService.updateItem(1L, updateDto, 1L));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void createItem_ValidItem_ReturnsItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.createItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }
}
