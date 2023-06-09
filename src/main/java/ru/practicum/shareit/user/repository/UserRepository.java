package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user, Long id);

    Optional<User> findUserById(Long id);

    List<User> findAll();

    void deleteUsersById(Long id);

    boolean emailExist(String email);

    boolean userExist(Long id);
}
