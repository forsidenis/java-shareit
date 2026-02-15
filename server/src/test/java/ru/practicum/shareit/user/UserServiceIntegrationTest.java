package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void createUser_ValidUser_ReturnsUserDto() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        var result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    public void updateUser_ValidUpdate_ReturnsUpdatedUser() {
        UserDto createDto = UserDto.builder()
                .name("Original Name")
                .email("original@example.com")
                .build();
        var createdUser = userService.createUser(createDto);

        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        var result = userService.updateUser(createdUser.getId(), updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    public void getAllUsers_ReturnsListOfUsers() {
        userService.createUser(UserDto.builder()
                .name("User1")
                .email("user1@example.com")
                .build());

        userService.createUser(UserDto.builder()
                .name("User2")
                .email("user2@example.com")
                .build());

        var result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }
}
