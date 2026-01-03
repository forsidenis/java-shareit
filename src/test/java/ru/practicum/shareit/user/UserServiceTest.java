package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;
    private User user1;
    private User user2;
    private UserDto userDto1;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);

        user1 = new User(1L, "John Doe", "john@example.com");
        user2 = new User(2L, "Jane Smith", "jane@example.com");
        userDto1 = new UserDto(1L, "John Doe", "john@example.com");
    }

    @Test
    public void createUser_success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto result = userService.createUser(userDto1);

        assertNotNull(result);
        assertEquals(userDto1.getName(), result.getName());
        assertEquals(userDto1.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void createUser_duplicateEmail_throwsException() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1));

        assertThrows(ConflictException.class, () -> userService.createUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUser_success() {
        UserDto updateDto = new UserDto(null, "John Updated", null);
        User updatedUser = new User(1L, "John Updated", "john@example.com");

        when(userRepository.findById(1L)).thenReturn(user1);
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("John Updated", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    public void updateUser_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                userService.updateUser(99L, new UserDto(null, "Test", "test@test.com")));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    public void updateUser_duplicateEmail_throwsException() {
        UserDto updateDto = new UserDto(null, "John Updated", "jane@example.com");
        when(userRepository.findById(1L)).thenReturn(user1);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        assertThrows(ConflictException.class, () ->
                userService.updateUser(1L, updateDto));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    public void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(user1);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void getUserById_notFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    public void getAllUsers_success() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
    }

    @Test
    public void deleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(user1);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_notFound_noException() {
        when(userRepository.findById(99L)).thenReturn(null);

        assertDoesNotThrow(() -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
