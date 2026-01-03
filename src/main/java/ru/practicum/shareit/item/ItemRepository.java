package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item) {
        if (item == null || !items.containsKey(item.getId())) {
            return null;
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        if (id == null) {
            return null;
        }
        return items.get(id);
    }

    public List<Item> findByOwnerId(Long ownerId) {
        if (ownerId == null) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getOwner() != null && ownerId.equals(item.getOwner().getId()))
                .collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase().trim();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(searchText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public void clear() {
        items.clear();
        idCounter = 1;
    }
}
