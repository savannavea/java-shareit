package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemServiceImpl;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("email2@email.com")
                .build();

        LocalDateTime created = LocalDateTime.now();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(owner)
                .created(created)
                .build();

        booker = User.builder()
                .id(1L)
                .name("booker")
                .email("email2@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .itemRequest(itemRequest)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(Instant.now())
                .end(Instant.now().plusSeconds(2))
                .booker(booker)
                .item(item)
                .status(Status.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("comment1")
                .author(booker)
                .item(item)
                .created(Instant.now())
                .build();

    }

    @Test
    void testCreateItem() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(itemRequestRepository.findById(ItemMapper.toItemDto(item).getRequestId()))
                .thenReturn(Optional.of(itemRequest));

        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto returnedItem = itemServiceImpl.create(owner.getId(), ItemMapper.toItemDto(item));

        assertEquals(returnedItem.getId(), 1L);
        assertEquals(returnedItem.getName(), "item");
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void testAddComment() {
        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(eq(item.getId()),
                eq(booker.getId()), eq(Status.APPROVED), any(Instant.class)))
                .thenReturn(Optional.of(booking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        itemServiceImpl.addComment(booker.getId(), item.getId(), CommentMapper.toCommentDto(comment));

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

    @Test
    void testUpdateItem() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(itemRepository.findOwnerIdByItemId(item.getId()))
                .thenReturn(Optional.of(item.getId()));

        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto returnedItem = itemServiceImpl.update(owner.getId(), ItemMapper.toItemDto(item), item.getId());

        assertEquals(returnedItem.getId(), 1L);
        assertEquals(returnedItem.getName(), "item");
        verify(itemRepository, times(1))
                .save(item);
    }

    @Test
    void testGetById() {
        List<Comment> comments = new ArrayList<>();
        item.setOwner(owner);
        comments.add(comment);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(comments);

        ItemDto itemDto = itemServiceImpl.getById(owner.getId(), item.getId());

        assertEquals(itemDto.getId(), 1L);
        assertEquals(itemDto.getName(), "item");
        verify(itemRepository)
                .findById(item.getId());

    }
}