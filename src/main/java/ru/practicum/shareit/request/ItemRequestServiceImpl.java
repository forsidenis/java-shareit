package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Creating item request for user: {}", userId);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Getting item requests for user: {}", userId);
        return Collections.emptyList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        log.info("Getting all item requests for user: {}", userId);
        return Collections.emptyList();
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        log.info("Getting item request {} for user: {}", requestId, userId);
        return new ItemRequestDto();
    }
}
