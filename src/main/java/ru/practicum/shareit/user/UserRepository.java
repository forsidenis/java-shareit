package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter++);
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user == null || !users.containsKey(user.getId())) {
            return null;
        }
        users.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        if (id == null) {
            return null;
        }
        return users.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(Long id) {
        if (id != null) {
            users.remove(id);
        }
    }

    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return users.containsKey(id);
    }

    public void clear() {
        users.clear();
        idCounter = 1; // Важно сбросить счетчик!
    }
}
