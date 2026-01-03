package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    private UserRepository userRepository;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        userRepository = new UserRepository();
        user1 = new User(null, "John", "john@example.com");
        user2 = new User(null, "Jane", "jane@example.com");
    }

    @Test
    public void save_newUser_assignsId() {
        User saved = userRepository.save(user1);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getName());
        assertEquals("john@example.com", saved.getEmail());
    }

    @Test
    public void save_multipleUsers_assignsIncrementalIds() {
        User saved1 = userRepository.save(user1);
        User saved2 = userRepository.save(user2);

        assertEquals(1L, saved1.getId());
        assertEquals(2L, saved2.getId());
    }

    @Test
    public void findById_exists_returnsUser() {
        User saved = userRepository.save(user1);

        User found = userRepository.findById(saved.getId()); // Убрали Optional<>

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    public void findById_notExists_returnsNull() {
        User found = userRepository.findById(999L); // Убрали Optional<>

        assertNull(found);
    }

    @Test
    public void findAll_multipleUsers_returnsAll() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    public void update_exists_updatesUser() {
        User saved = userRepository.save(user1);
        saved.setName("Updated Name");

        User updated = userRepository.update(saved);

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("john@example.com", updated.getEmail());
    }

    @Test
    public void update_notExists_returnsNull() {
        User nonExistingUser = new User(999L, "Non-existing", "test@test.com");

        User updated = userRepository.update(nonExistingUser);

        assertNull(updated);
    }

    @Test
    public void deleteById_exists_removesUser() {
        User saved = userRepository.save(user1);

        userRepository.deleteById(saved.getId());

        User found = userRepository.findById(saved.getId());
        assertNull(found);
    }

    @Test
    public void deleteById_notExists_doesNothing() {
        assertDoesNotThrow(() -> userRepository.deleteById(999L));
    }

    @Test
    public void existsById_exists_returnsTrue() {
        User saved = userRepository.save(user1);

        assertTrue(userRepository.existsById(saved.getId()));
    }

    @Test
    public void existsById_notExists_returnsFalse() {
        assertFalse(userRepository.existsById(999L));
    }

    @Test
    public void clear_removesAllUsers() {
        userRepository.save(user1);
        userRepository.save(user2);

        userRepository.clear();

        List<User> users = userRepository.findAll();
        assertTrue(users.isEmpty());
    }
}
