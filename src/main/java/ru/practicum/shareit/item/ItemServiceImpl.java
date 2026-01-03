package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        var owner = userRepository.findById(userId);
        if (owner == null) {
            throw new RuntimeException("User not found");
        }

        // Проверяем обязательные поля
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Item description cannot be empty");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available status must be specified");
        }

        Item item = ItemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new RuntimeException("Item not found");
        }

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new SecurityException("User is not the owner of the item");
        }

        // Проверяем, что если поля переданы, то они валидные
        if (itemDto.getName() != null) {
            if (itemDto.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Item name cannot be empty");
            }
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().trim().isEmpty()) {
                throw new IllegalArgumentException("Item description cannot be empty");
            }
        }

        // Используем маппер для правильного обновления
        var owner = userRepository.findById(userId);
        Item updatedItem = ItemMapper.toItem(itemDto, existingItem, owner);

        Item savedItem = itemRepository.update(updatedItem);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
