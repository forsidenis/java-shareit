package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
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
    public void createUser_DuplicateEmail_ThrowsConflictException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void createUser_ValidUser_ReturnsUserDto() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_EmptyName_UpdatesSuccessfully() {
        UserDto updateDto = UserDto.builder()
                .name("")  // Пустое имя
                .email("new@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());  // Имя не изменилось
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_OnlySpacesInName_NameNotUpdated() {
        UserDto updateDto = UserDto.builder()
                .name("   ")  // Только пробелы
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());  // Имя не изменилось
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_ValidName_UpdatesSuccessfully() {
        User updatedUser = User.builder()
                .id(1L)
                .name("New Name")
                .email("test@example.com")
                .build();

        UserDto updateDto = UserDto.builder()
                .name("New Name")  // Новое имя
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());  // Имя изменилось
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_DuplicateEmail_ThrowsConflictException() {
        UserDto updateDto = UserDto.builder()
                .email("existing@example.com")
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .name("Another User")
                .email("existing@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(anotherUser));

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void getUserById_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    public void updateUser_InvalidEmailFormat_DoesNotThrowException() {
        UserDto updateDto = UserDto.builder()
                .email("invalid-email")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("invalid-email")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updateUser(1L, updateDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getAllUsers_ReturnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(user));

        var result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    public void deleteUser_UserExists_DeletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
