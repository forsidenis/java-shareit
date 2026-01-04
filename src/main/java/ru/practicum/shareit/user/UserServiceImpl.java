package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Валидация для создания
        validateUserForCreate(userDto);

        // Проверка уникальности email при создании
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже используется");
        }

        User user = UserMapper.toEntity(userDto);
        try {
            User savedUser = userRepository.save(user);
            return UserMapper.toDto(savedUser);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        // Обновляем только переданные непустые поля
        if (userDto.getName() != null && !userDto.getName().trim().isEmpty()) {
            existingUser.setName(userDto.getName().trim());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().trim().isEmpty()) {
            String newEmail = userDto.getEmail().trim();

            // Проверяем формат email
            if (!newEmail.contains("@")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный формат email");
            }

            // Проверяем, что email не занят другим пользователем
            if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email уже используется другим пользователем");
            }
            existingUser.setEmail(newEmail);
        }

        try {
            User updatedUser = userRepository.update(existingUser);
            return UserMapper.toDto(updatedUser);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.findById(userId).isPresent()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateUserForCreate(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email не может быть пустым");
        }
        if (!userDto.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный формат email");
        }
    }
}
