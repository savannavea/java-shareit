package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Item create(Long userId, Item item) {
        Long itemId = idGenerator.getAndIncrement();
        item.setId(itemId);
        User owner = User.builder().id(userId).build();
        item.setOwner(owner);
        items.put(itemId, item);
        return items.get(item.getId());
    }

    @Override
    public Item update(Item item, Long id) {
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<ItemDto> findItemsByQuery(String query) {
        List<ItemDto> listItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.isAvailable() && (item.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                    item.getName().toLowerCase().contains(query.toLowerCase()))) {
                listItems.add(ItemMapper.toItemDto(item));
            }
        }
        return listItems;
    }

    @Override
    public void deleteItemById(Long id) {
        items.remove(id);
    }
}