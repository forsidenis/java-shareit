package ru.practicum.shareit.request;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemRequestRepository implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        if (itemRequest.getId() == null) {
            itemRequest.setId(counter.getAndIncrement());
        }
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public List<ItemRequest> findAllByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> request.getRequestor() != null &&
                        request.getRequestor().getId().equals(requestorId))
                .toList();
    }

    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

    @Override
    public void deleteById(Long id) {
        requests.remove(id);
    }
}
