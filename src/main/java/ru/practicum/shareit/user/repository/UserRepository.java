package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private Long id = 0L;

    public List<User> getAllUsers() {
        return users;
    }

    public User getUserById(Long userId) throws Exception {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ServerException(String.format("Пользователь с Id=%d не найден", userId)));
    }

    public User addNewUser(User user) {
        user.setId(++id);
        users.add(user);
        return user;
    }

    public User updateUser(Long userId, UserDto userDto) throws Exception {
        List<String> emails = users.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        for (User user : users) {
            if (user.getId().equals(userId)) {
                if (userDto.getName() != null) {
                    user.setName(userDto.getName());
                }
                if (userDto.getEmail() != null) {
                    if (emails.contains(userDto.getEmail())) {
                        throw new Exception(
                                String.format("Пользователь с Email=%s уже существует", userDto.getEmail()));
                    } else {
                        user.setEmail(userDto.getEmail());
                    }
                }
                return user;
            }
        }
        return null;
    }

    public void deleteUser(Long userId) {
        if (users != null) {
            if (!(users.removeIf(item -> item.getId().equals(userId))))
                throw new ValidationException(String.format("Пользователь с ID=%d не найден", userId));
        }
    }
}
