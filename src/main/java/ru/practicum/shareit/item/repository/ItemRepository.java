package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            " OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))" +
            "AND i.available=TRUE")
    List<Item> findItemsByQuery(String text);

    @Query("SELECT u.id " +
            "FROM Item AS it " +
            "JOIN it.owner AS u " +
            "WHERE it.id = ?1")
    Optional<Long> findOwnerIdByItemId(Long itemId);
}