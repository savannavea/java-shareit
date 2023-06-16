package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.getUserById(userId);
        return ItemMapper.toItemDto(itemRepository.create(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long id) {
        userService.getUserById(userId);
        Item item = itemRepository.findItemById(id);
        Long ownerId = item.getOwner().getId();
        if (!userId.equals(ownerId)) {
            throw new NotFoundException(String.format("User with id %d does not own item with id %d", userId, id));
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Name cannot be empty");
            }
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Description cannot be empty");
            }
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.update(item, id));
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemRepository.findItemById(id));
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        userService.getUserById(userId);
        return itemRepository
                .findAllItems()
                .stream()
                .filter(Item -> Item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByQuery(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItemByQuery(query);
    }

    @Override
    public void deleteItemsById(Long id) {
        itemRepository.deleteItemsById(id);
    }
}
