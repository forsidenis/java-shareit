package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        if (itemDto == null || owner == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item toItem(ItemDto itemDto, Item existingItem, User owner) {
        if (existingItem == null || owner == null) {
            return null;
        }
        Item item = new Item();
        item.setId(existingItem.getId());
        item.setName(itemDto.getName() != null ? itemDto.getName() : existingItem.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : existingItem.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : existingItem.getAvailable());
        item.setOwner(owner);
        item.setRequest(existingItem.getRequest());
        return item;
    }
}
