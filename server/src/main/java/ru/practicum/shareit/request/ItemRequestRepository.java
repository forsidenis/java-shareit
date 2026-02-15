package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    @Query("SELECT r FROM ItemRequest r " +
            "WHERE r.requestor.id != :userId " +
            "ORDER BY r.created DESC")
    List<ItemRequest> findAllByRequestorIdNot(Long userId, Pageable pageable);
}
