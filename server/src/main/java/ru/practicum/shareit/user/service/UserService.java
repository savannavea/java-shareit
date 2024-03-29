package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userdto);

    UserDto update(UserDto userDto, Long id);

    List<UserDto> getAll();

    UserDto getUserById(Long id);

    void deleteUsersById(Long id);

    User getUserOrElseThrow(Long userId);
}