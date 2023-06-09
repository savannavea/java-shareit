package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EmailExists;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Got request to create user {}", userDto);
        if (userDto.getEmail().isBlank() || !userDto.getEmail().contains("@")) {
            throw new BadRequestException("Email cannot be empty and must contain @");
        }
        checkEmailDuplicate(userDto.getEmail());
        return UserMapper.toUserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {

        checkUserExist(id);

        User user = UserMapper.toUser(getUserById(id));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            checkEmailDuplicate(userDto.getEmail());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        User userUpdated = userRepository.update(user, id);

        log.info("Обновлён пользователь: {}", userUpdated);
        return UserMapper.toUserDto(userUpdated);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper :: toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User's id %d doesn't found!" + id)));
    }

    @Override
    public void deleteUsersById(Long id) {
        userRepository.deleteUsersById(id);
    }

    private void checkEmailDuplicate(String email) {
        if (userRepository.emailExist(email)) {
            throw new EmailExists(
                    String.format("User with email address: %s already registered", email));
        }
    }

    private void checkUserExist(long id) {
        if (!userRepository.userExist(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

}
