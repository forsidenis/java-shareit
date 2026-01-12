package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        // Проверка на пустой текст вынесена в сервис
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> (item.getName() != null &&
                        item.getName().toLowerCase().contains(lowerText)) ||
                        (item.getDescription() != null &&
                                item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null || !items.containsKey(item.getId())) {
            throw new NotFoundException("Item not found");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> findByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }
}
