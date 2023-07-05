package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    //Optional<User> findByEmailContainingIgnoreCase(String email);
}
