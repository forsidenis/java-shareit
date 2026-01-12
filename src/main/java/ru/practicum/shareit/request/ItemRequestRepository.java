package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository {
    ItemRequest save(ItemRequest itemRequest);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAllByRequestorId(Long requestorId);

    List<ItemRequest> findAll();

    void deleteById(Long id);
}
