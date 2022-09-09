package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long userId) throws Exception;

    User addNewUser(User user);

    User updateUser(Long userId, UserDto userDto) throws Exception;

    void deleteUser(Long userId);

}
