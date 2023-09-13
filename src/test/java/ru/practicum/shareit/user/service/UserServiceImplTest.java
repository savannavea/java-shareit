package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String TEST_NAME = "Kate";
    public static final String TEST_EMAIL = "Kate@yandex.ru";
    public static final long TEST_ID = 1L;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceimpl;

    private User expectedUser;

    @BeforeEach
    void setUser() {
        expectedUser = User.builder()
                .id(1L)
                .name("Kate")
                .email("Kate@yandex.ru")
                .build();
    }

    @Test
    void testCreateUserOk() {
        Mockito
                .when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);

        UserDto returnedUser = userServiceimpl.create(UserMapper.toUserDto(expectedUser));


        Assertions.assertEquals(TEST_ID, returnedUser.getId());
        Assertions.assertEquals(TEST_NAME, returnedUser.getName());
        Assertions.assertEquals(TEST_EMAIL, returnedUser.getEmail());

        Mockito
                .verify(userRepository)
                .save(expectedUser);
    }

    @Test
    void testUpdateValidUser() {
        Mockito
                .when(userRepository.save(expectedUser))
                .thenReturn(expectedUser);
        Mockito
                .when(userRepository.findById(expectedUser.getId()))
                .thenReturn(Optional.of(expectedUser));

        UserDto returnedUser = userServiceimpl.update(UserMapper.toUserDto(expectedUser), expectedUser.getId());

        Assertions.assertEquals(UserMapper.toUser(returnedUser), expectedUser);

        Mockito
                .verify(userRepository)
                .save(expectedUser);
        Mockito
                .verify(userRepository)
                .findById(expectedUser.getId());
    }

    @Test
    void testUpdateInvalidUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userServiceimpl.getUserById(expectedUser.getId()));

        Assertions.assertEquals("User's id 1 doesn't found!", exception.getMessage());
        Mockito
                .verify(userRepository, never())
                .save(expectedUser);
        Mockito
                .verify(userRepository)
                .findById(expectedUser.getId());

    }

    @Test
    void testGetAllUsers() {
        List<User> expectedUsers = List.of(expectedUser);
        List<UserDto> expectedUserDto = expectedUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = userServiceimpl.getAll();

        Assertions.assertEquals(1, actualUsersDto.size());
        Assertions.assertEquals(expectedUserDto, actualUsersDto);
    }

    @Test
    void testGetUserById() {
        Mockito
                .when(userRepository.findById(expectedUser.getId()))
                .thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userServiceimpl.getUserById(expectedUser.getId());

        Assertions.assertEquals(UserMapper.toUser(actualUser), expectedUser);

    }

    @Test
    void testDeleteUser() {
        userServiceimpl.deleteUsersById(anyLong());

        Mockito
                .verify(userRepository, times(1))
                .deleteById(anyLong());
    }
}