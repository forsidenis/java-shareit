package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemDto itemDto, Long userId) {
        User owner = getUserById(userId);

        Item item = ItemMapper.toEntity(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId())
                    .ifPresent(item::setRequest);
        }

        Item savedItem = itemRepository.save(item);
        log.info("Created item with id: {}", savedItem.getId());
        return ItemMapper.toItemResponseDto(savedItem, null, null);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("User " + userId + " is not the owner of item " + itemId);
        }

        ItemMapper.updateEntity(item, itemUpdateDto);
        Item updatedItem = itemRepository.save(item);

        BookingInfoDto lastBooking = getLastBooking(itemId);
        BookingInfoDto nextBooking = getNextBooking(itemId);
        List<CommentDto> comments = getCommentsForItem(itemId);

        ItemResponseDto responseDto = ItemMapper.toItemResponseDto(updatedItem, lastBooking, nextBooking);
        responseDto.setComments(comments);

        log.info("Updated item with id: {}", updatedItem.getId());
        return responseDto;
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        BookingInfoDto lastBooking = null;
        BookingInfoDto nextBooking = null;
        List<CommentDto> comments = getCommentsForItem(itemId);

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = getLastBooking(itemId);
            nextBooking = getNextBooking(itemId);
        }

        ItemResponseDto responseDto = ItemMapper.toItemResponseDto(item, lastBooking, nextBooking);
        responseDto.setComments(comments);

        return responseDto;
    }

    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findByOwnerId(userId, pageable);

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, BookingInfoDto> lastBookings = bookingRepository.findByItemIdInAndEndBeforeAndStatusOrderByEndDesc(
                        itemIds, LocalDateTime.now(), BookingStatus.APPROVED).stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingInfoDto,
                        (first, second) -> first // keep first if duplicate
                ));

        Map<Long, BookingInfoDto> nextBookings = bookingRepository.findByItemIdInAndStartAfterAndStatusOrderByStartAsc(
                        itemIds, LocalDateTime.now(), BookingStatus.APPROVED).stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        BookingMapper::toBookingInfoDto,
                        (first, second) -> first // keep first if duplicate
                ));

        Map<Long, List<CommentDto>> commentsByItem = commentRepository.findByItemIdIn(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> {
                    ItemResponseDto dto = ItemMapper.toItemResponseDto(
                            item,
                            lastBookings.get(item.getId()),
                            nextBookings.get(item.getId())
                    );
                    dto.setComments(commentsByItem.getOrDefault(item.getId(), Collections.emptyList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text, Long userId) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        getUserById(userId);
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.searchAvailableItems(text, pageable);

        return items.stream()
                .map(item -> ItemMapper.toItemResponseDto(item, null, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, CommentRequestDto commentRequestDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + itemId));

        User author = getUserById(userId);

        List<ru.practicum.shareit.booking.Booking> bookings = bookingRepository
                .findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new ValidationException("User " + userId + " has not booked item " + itemId + " in the past");
        }

        Comment comment = CommentMapper.toComment(commentRequestDto.getText(), item, author);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    private BookingInfoDto getLastBooking(Long itemId) {
        return bookingRepository
                .findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getNextBooking(Long itemId) {
        return bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
