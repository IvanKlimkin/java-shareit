package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        return userRepository.getUserById(userId);
    }

    @Override
    public User addNewUser(User user) {
        return userRepository.addNewUser(user);
    }

    @Override
    public User updateUser(Long userId, UserDto userDto) throws Exception {
        List<Long> usersId = userRepository.getAllUsers().stream().map(User::getId).collect(Collectors.toList());
        if (usersId.contains(userId)) {
            return userRepository.updateUser(userId, userDto);
        } else {
            throw new ValidationException(String.format("Пользователь с ID=%d не найден", userId));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
