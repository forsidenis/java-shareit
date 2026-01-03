package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    @Test
    public void toUserDto_validUser_returnsDto() {
        User user = new User(1L, "John", "john@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    public void toUser_validDto_returnsUser() {
        UserDto dto = new UserDto(1L, "John", "john@example.com");

        User user = UserMapper.toUser(dto);

        assertEquals(1L, user.getId());
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }
}
