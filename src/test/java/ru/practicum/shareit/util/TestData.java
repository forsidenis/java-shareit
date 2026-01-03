package ru.practicum.shareit.util;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class TestData {

    public static User createTestUser(Long id, String name, String email) {
        return new User(id, name, email);
    }

    public static UserDto createTestUserDto(Long id, String name, String email) {
        return new UserDto(id, name, email);
    }

    public static Item createTestItem(Long id, String name, String description, Boolean available, User owner) {
        return new Item(id, name, description, available, owner, null);
    }

    public static ItemDto createTestItemDto(Long id, String name, String description, Boolean available) {
        return new ItemDto(id, name, description, available, null);
    }
}
