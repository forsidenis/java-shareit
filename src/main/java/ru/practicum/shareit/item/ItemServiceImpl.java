package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemResponseDto createItem(ItemDto itemDto, Long userId) {
        User owner = getUserById(userId);
        Item item = ItemMapper.toEntity(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toResponseDto(savedItem);
    }

    @Override
    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        // Проверяем, что пользователь является владельцем
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Вещь с ID " + itemId + " не найдена у пользователя с ID " + userId);
        }

        // Обновляем поля
        ItemMapper.updateEntity(item, itemUpdateDto);

        Item updatedItem = itemRepository.update(item);
        return ItemMapper.toResponseDto(updatedItem);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        return ItemMapper.toResponseDto(item);
    }

    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findByOwner(userId);
        return items.stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text, Long userId) {
        getUserById(userId); // Проверяем пользователя

        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.search(text);
        return items.stream()
                .map(ItemMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return UserMapper.toEntity(userService.getUserById(userId));
    }
}
