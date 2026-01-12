package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteById(Long id);

    List<User> findAll();

    boolean existsByEmailAndIdNot(String email, Long id);
}