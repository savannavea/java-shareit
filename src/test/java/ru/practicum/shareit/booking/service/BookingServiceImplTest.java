package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;
    private User booker;
    private ItemDto itemDto;
    private Item item;
    private Booking booking;


    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("email2@email.com")
                .build();

        booker = User.builder()
                .id(1L)
                .name("booker")
                .email("email2@email.com")
                .build();

        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requester(owner)
                .created(created)
                .build();

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .owner(owner)
                .available(true)
                .itemRequest(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .booker(UserMapper.toUserDto(booker))
                .itemId(1L)
                .build();

        booking = BookingMapper.toBooking(bookingDto);
    }

    @Test
    void testCreateBooking() {
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto savedDto = bookingService.create(booker.getId(), BookingMapper.toBookingDto(booking));

        booking.setStatus(Status.WAITING);
        assertThat(savedDto, notNullValue());
        assertThat(savedDto.getStatus(), equalTo(Status.WAITING));
        assertThat(savedDto, equalTo(BookingMapper.toBookingDto(booking)));
        verify(itemRepository).findById(item.getId());

    }

    @Test
    void testApproveBooking() {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto result = bookingService.approve(owner.getId(), booking.getId(), "true");

        assertThat(result.getStatus(), equalTo(Status.APPROVED));
        verify(bookingRepository).findById(booking.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void testGetBookingById() {
        item.setOwner(owner);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto returnedBookingDto = bookingService.getById(booking.getId(),
                owner.getId());

        assertNotNull(returnedBookingDto);
        assertEquals(booking.getId(), returnedBookingDto.getId());

        verify(bookingRepository, atLeast(1))
                .findById(booking.getId());

    }

}
