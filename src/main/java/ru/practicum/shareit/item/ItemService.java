package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(ItemDto itemDto, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemResponseDto getItemById(Long itemId);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getAllItems(Long userId);

    List<ItemResponseDto> searchItems(String text, Long userId);
}
