package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Got request to create user {}", userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {

        User user = getUserOrElseThrow(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(user.getEmail());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User userUpdated = userRepository.save(user);

        log.info("User updated: {}", userUpdated);
        return UserMapper.toUserDto(userUpdated);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository
                .findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", id)));
    }

    @Override
    public void deleteUsersById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserOrElseThrow(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", id)));
    }
}