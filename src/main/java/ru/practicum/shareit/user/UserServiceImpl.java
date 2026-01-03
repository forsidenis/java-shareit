package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // Проверка уникальности email
        userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(userDto.getEmail()))
                .findFirst()
                .ifPresent(user -> {
                    throw new ConflictException("Email already exists");
                });

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        // Проверяем, что если email передан, то он валидный
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }
            // Проверяем, что новый email не занят другим пользователем
            if (!existingUser.getEmail().equals(userDto.getEmail())) {
                boolean emailExists = userRepository.findAll().stream()
                        .anyMatch(user -> user.getEmail().equals(userDto.getEmail()));
                if (emailExists) {
                    throw new ConflictException("Email already exists");
                }
            }
        }

        // Обновляем только переданные поля
        if (userDto.getName() != null) {
            if (userDto.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.update(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            userRepository.deleteById(userId);
        }
    }
}
