package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(ItemDto itemDto, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long userId);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getAllItems(Long userId);

    List<ItemResponseDto> searchItems(String text, Long userId);

    CommentDto addComment(Long itemId, CommentRequestDto commentRequestDto, Long userId);
}
