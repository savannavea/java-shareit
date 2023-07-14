package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = getUserOrElseThrow(userId);
        Item item = ItemMapper.toItemWithOwner(itemDto, user);
        if (itemDto.getRequestId() != null) {
            Long requestId = itemDto.getRequestId();
            ItemRequest itemRequest = itemRequestRepository
                    .findById(requestId)
                    .orElseThrow(() -> new NotFoundException(String.format(
                            "ItemRequest's id %d doesn't found!", requestId)));
            item.setItemRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = getUserOrElseThrow(userId);
        Item item = getItemOrElseThrow(itemId);
        checkBooker(userId, itemId);
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(Instant.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long id) {
        getUserOrElseThrow(userId);
        Item item = getItemOrElseThrow(id);
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
    public ItemDto getById(Long userId, Long id) {
        Item item = itemRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Item's id %d doesn't found!", id)));

        ItemDto itemDto = ItemMapper.toItemDto(item);

        if (Objects.equals(item.getOwner().getId(), userId)) {

            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(id, Status.APPROVED, Instant.now());
            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(id, Status.APPROVED, Instant.now());

            lastBooking.ifPresent(lb -> itemDto.setLastBooking(BookingMapper.toBookingItemDto(lb)));
            nextBooking.ifPresent(lb -> itemDto.setNextBooking(BookingMapper.toBookingItemDto(lb)));
        }

        List<Comment> commentList = commentRepository.findAllByItemId(id);

        if (!commentList.isEmpty()) {
            itemDto.setComments(CommentMapper.toICommentDtoList(commentList));
        } else {
            itemDto.setComments(Collections.emptyList());
        }

        return itemDto;
    }

    @Override
    public List<ItemDto> getAllByUserId(Long userId) {
        userService.getUserById(userId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(item,
                        getLastBooking(item),
                        getNextBooking(item),
                        getAllCommentsByItemId(item.getId()))
                )
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

    @Override
    public List<CommentDto> getAllCommentsByItemId(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private User getUserOrElseThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", userId)));
    }

    private Item getItemOrElseThrow(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item's id %d doesn't found!" + itemId)));
    }

    private ItemOwnerDto getLastBooking(Item item) {
        return bookingRepository
                .findLastBooking(item.getId(), Instant.now())
                .map(BookingMapper::toBookingItemDto)
                .orElse(null);
    }

    private ItemOwnerDto getNextBooking(Item item) {
        return bookingRepository
                .findNextBooking(item.getId(), Instant.now())
                .map(BookingMapper::toBookingItemDto)
                .orElse(null);
    }

    private void checkBooker(Long userId, Long itemId) {
        Instant dateTime = Instant.now();
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId,
                userId, Status.APPROVED, dateTime);

        if (booking.isEmpty()) {
            throw new BadRequestException(
                    String.format("User with id %s did not rent an item with id %s", userId, itemId));
        }
    }
}