package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Item item, Long id);

    List<Item> findAllItems();

    Item findItemById(Long id);

    List<ItemDto> findItemByQuery(String query);

    void deleteItemsById(Long id);
}