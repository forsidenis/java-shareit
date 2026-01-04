package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void createUser_DuplicateEmail_ThrowsConflictException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(userDto));
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ValidUser_ReturnsUserDto() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_EmptyName_UpdatesSuccessfully() {
        UserDto updateDto = UserDto.builder()
                .name("")  // Пустое имя
                .email("new@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("new@example.com", 1L)).thenReturn(false);
        when(userRepository.update(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        // Имя должно остаться старым, так как новое пустое
        assertEquals("Test User", result.getName());
        // Email должен обновиться
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_OnlySpacesInName_NameNotUpdated() {
        UserDto updateDto = UserDto.builder()
                .name("   ")  // Только пробелы
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.update(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        // Имя должно остаться старым, так как новое содержит только пробелы
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_ValidName_UpdatesSuccessfully() {
        UserDto updateDto = UserDto.builder()
                .name("New Name")  // Новое имя
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.update(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsConflictException() {
        UserDto updateDto = UserDto.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("existing@example.com", 1L)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void getUserById_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_InvalidEmailFormat_ThrowsBadRequest() {
        UserDto updateDto = UserDto.builder()
                .email("invalid-email")  // Нет @
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository, never()).update(any(User.class));
    }
}
