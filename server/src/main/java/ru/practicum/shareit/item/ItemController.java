package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody ItemUpdateDto itemUpdateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.updateItem(itemId, itemUpdateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(
            @RequestParam(required = false) String text,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addComment(itemId, commentRequestDto, userId);
    }
}
