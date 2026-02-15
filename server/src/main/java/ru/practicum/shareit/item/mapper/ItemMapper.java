package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable());

        if (item.getOwner() != null) {
            builder.ownerId(item.getOwner().getId());
        }

        if (item.getRequest() != null) {
            builder.requestId(item.getRequest().getId());
        }

        return builder.build();
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingInfoDto lastBooking,
                                                    BookingInfoDto nextBooking) {
        if (item == null) {
            return null;
        }

        ItemResponseDto.ItemResponseDtoBuilder builder = ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking);

        if (item.getOwner() != null) {
            builder.ownerId(item.getOwner().getId());
        }

        if (item.getRequest() != null) {
            builder.requestId(item.getRequest().getId());
        }

        return builder.build();
    }

    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingInfoDto lastBooking,
                                                    BookingInfoDto nextBooking,
                                                    List<CommentDto> comments) {
        ItemResponseDto dto = toItemResponseDto(item, lastBooking, nextBooking);
        if (dto != null) {
            dto.setComments(comments);
        }
        return dto;
    }

    public static Item toEntity(ItemDto itemDto, User owner) {
        if (itemDto == null) {
            return null;
        }

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static void updateEntity(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
    }
}
