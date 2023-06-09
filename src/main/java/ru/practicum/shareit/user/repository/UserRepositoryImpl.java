package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public User create(User user) {
        Long userId = idGenerator.getAndIncrement();
        user.setId(userId);
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user, Long id) {
        user.setId(id);
        users.put(id, user);
        return users.get(user.getId());
    }

    @Override
    public  Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUsersById(Long id) {
        users.remove(id);
    }

    @Override
    public boolean emailExist(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean userExist(Long id) {
        for (User user : users.values()) {
            if (user.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
