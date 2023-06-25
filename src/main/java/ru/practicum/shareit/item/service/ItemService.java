package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long id);

    ItemDto getById(Long id);

    List<ItemDto> findAllByUserId(Long userId);

    List<ItemDto> getByQuery(String query);

    void deleteItemsById(Long id);
}
