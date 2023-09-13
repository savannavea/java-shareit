package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

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
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User owner;
    private UserDto ownerDto;
    private User booker;
    private Item item;
    private BookingDto booking;
    private Comment comment;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("email2@email.com")
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
                .build();

        booking = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusWeeks(2))
                .booker(UserMapper.toUserDto(booker))
                .itemId(1L)
                .build();

        ownerDto = UserMapper.toUserDto(owner);
        comment = Comment.builder()
                .id(1L)
                .text("comment1")
                .author(booker)
                .item(item)
                .build();

    }

    @Test
    void addItem() {

        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto returnedItem = itemService.create(owner.getId(), ItemMapper.toItemDto(item));

        verify(itemRepository).save(item);
    }

}