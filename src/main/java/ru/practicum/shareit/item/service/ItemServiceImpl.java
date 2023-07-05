package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        Item item = ItemMapper.toItemWithOwner(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = checkUser(userId);
        Item item = checkItem(itemId);
//добавить проверку на вадение вещью
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        return CommentMapper.tocommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long id) {
        checkUser(userId);
        Item item = checkItem(id);
        Optional<Long> ownerId = itemRepository.findOwnerIdByItemId(item.getId());
        if (ownerId.isEmpty() || !ownerId.get().equals(item.getOwner().getId())) {
            throw new NotFoundException(String.format("User with id %d does not own item with id %d", userId, id));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getById(Long id) {
        return itemRepository
                .findById(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item's id %d doesn't found!" + id));
    }

    @Override
    public List<ItemDto> findAllByUserId(Long userId) {
        userService.getUserById(userId);
        return itemRepository
                .findItemByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByQuery(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository
                .findItemsByQuery(query)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemsById(Long id) {
        itemRepository.deleteById(id);
    }

    private User checkUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User's id %d doesn't found!" + userId));
    }

    private Item checkItem(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item's id %d doesn't found!" + itemId));
    }

    /*  private Booking checkBooking(Long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Item's id %d doesn't found!" + bookingId));
    }*/
}