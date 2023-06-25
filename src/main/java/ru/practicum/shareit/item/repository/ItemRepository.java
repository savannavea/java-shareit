package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Item item, Long id);

    List<Item> findAllItems();

    Optional<Item> findItemById(Long id);

    List<ItemDto> findItemsByQuery(String query);

    void deleteItemById(Long id);
}