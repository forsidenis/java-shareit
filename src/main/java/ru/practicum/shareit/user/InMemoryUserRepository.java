package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long idCounter = 1L;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            // При создании: проверяем уникальность email
            if (existsByEmail(user.getEmail())) {
                throw new IllegalStateException("Email уже используется");
            }
            user.setId(idCounter++);
        } else {
            // При обновлении: проверяем уникальность email, кроме текущего пользователя
            User existingUser = users.get(user.getId());
            if (existingUser != null) {
                // Если email меняется и уже используется другим пользователем
                if (!existingUser.getEmail().equals(user.getEmail()) &&
                        existsByEmailAndIdNot(user.getEmail(), user.getId())) {
                    throw new IllegalStateException("Email уже используется другим пользователем");
                }
            }
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new NoSuchElementException("User not found");
        }
        return save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail() != null &&
                        user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail() != null &&
                        user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail() != null &&
                        user.getEmail().equalsIgnoreCase(email) &&
                        !user.getId().equals(id));
    }
}
