package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(Long ownerId);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            "and i.available=true")
    List<Item> findItemsByQuery(String text);

    @Query("select u.id " +
            "from Item as it " +
            "join it.owner as u " +
            "where it.id = ?1")
    Optional<Long> findOwnerIdByItemId(long itemId);
}