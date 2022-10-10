package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(Long userId) throws Exception {
        return userMapper.toDto(
                userRepository.findById(userId)
                        .orElseThrow(() -> new ServerException("Пользователь с таким ID отсутствует.")));
    }

    @Override
    public UserDto addNewUser(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) throws Exception {
        User user = userRepository.getReferenceById(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return userMapper.toDto(userRepository.save(user));

    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
