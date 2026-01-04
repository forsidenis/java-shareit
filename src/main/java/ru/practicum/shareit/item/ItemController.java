package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        // Явная валидация для createItem
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Статус доступности не может быть null");
        }

        ItemResponseDto createdItem = itemService.createItem(itemDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        // Проверяем, что имя не пустое, если передано и не равно null
        if (itemDto.getName() != null && itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        // Проверяем, что описание не пустое, если передано и не равно null
        if (itemDto.getDescription() != null && itemDto.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }

        ItemResponseDto updatedItem = itemService.updateItem(itemId, itemDto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        ItemResponseDto item;
        if (userId != null) {
            item = itemService.getItemById(itemId, userId);
        } else {
            item = itemService.getItemById(itemId);
        }
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> items = itemService.getAllItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(
            @RequestParam(required = false) String text,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> items = itemService.searchItems(text, userId);
        return ResponseEntity.ok(items);
    }
}
