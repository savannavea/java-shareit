package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.enums.State.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "start");
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;
    private User ownerInvalid;
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

        booking = Booking.builder()
                .id(1L)
                .start(Instant.now())
                .end(Instant.now())
                .booker(booker)
                .item(item)
                .build();
    }

    @Test
    void testCreateBooking() {
        booking.setStatus(Status.WAITING);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto savedDto = bookingService.create(booker.getId(), BookingMapper.toBookingDto(booking));

        assertThat(savedDto, notNullValue());
        assertThat(savedDto.getStatus(), equalTo(Status.WAITING));
        assertThat(savedDto, equalTo(BookingMapper.toBookingDto(booking)));
        verify(itemRepository).findById(item.getId());

    }

    @Test
    void testCreateBookingWithInvalidItem() {
        booking.setStatus(Status.WAITING);
        item.setAvailable(false);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.create(booker.getId(), BookingMapper.toBookingDto(booking)));

        assertEquals("Item is not available", exception.getMessage());
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void testCreateBookingWithInvalidBooker() {
        booking.setStatus(Status.WAITING);
        item.setOwner(booker);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.create(booker.getId(), BookingMapper.toBookingDto(booking)));

        assertEquals("Reservation not found", exception.getMessage());
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void testApproveBooking() {
        booking.setStatus(Status.WAITING);

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
    void testApproveBookingWithInvalidUserId() {
        booking.setStatus(Status.WAITING);
        ownerInvalid = User.builder()
                .id(3L)
                .name("owner")
                .email("email2@email.com")
                .build();

        when(userRepository.findById(ownerInvalid.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.approve(ownerInvalid.getId(), booking.getId(), "true"));

        assertEquals("Id of the user's item does not match the id of the owner of the item",
                exception.getMessage());
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void testApproveBookingWithInvalidStatus() {
        booking.setStatus(Status.APPROVED);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                bookingService.approve(owner.getId(), booking.getId(), "true"));

        assertEquals("The status has been confirmed by the owner before",
                exception.getMessage());
        verify(bookingRepository, never())
                .save(booking);
    }

    @Test
    void testGetBookingById() {
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

    @Test
    void testGetUserBookingsStateAll() {
        State state = ALL;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_Id(booker.getId(), page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository).findByBooker_Id(anyLong(), any());
    }

    @Test
    void testGetUserBookingsStatePast() {
        State state = PAST;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_IdAndEndIsBefore(booker.getId(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository).findByBooker_IdAndEndIsBefore(anyLong(),
                any(Instant.class), any());
    }

    @Test
    void testGetUserBookingsStateFuture() {
        State state = FUTURE;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_IdAndStartIsAfter(booker.getId(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository).findByBooker_IdAndStartIsAfter(anyLong(),
                any(Instant.class), any());
    }

    @Test
    void testGetUserBookingsStateCurrent() {
        State state = CURRENT;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                Instant.now(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository).findByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(Instant.class), any(Instant.class), any());
    }

    @Test
    void testGetUserBookingsStateWaiting() {
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_IdAndStatus(booker.getId(), Status.WAITING, page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), State.WAITING, 0, 2);

        verify(bookingRepository).findByBooker_IdAndStatus(anyLong(), any(), any());
    }

    @Test
    void testGetUserBookingsStateRejected() {
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker_IdAndStatus(booker.getId(), Status.REJECTED, page))
                .thenReturn(List.of(booking));

        bookingService.getUserBookings(booker.getId(), State.REJECTED, 0, 2);

        verify(bookingRepository).findByBooker_IdAndStatus(anyLong(), any(), any());
    }

    @Test
    void testGetItemsBookingsStateAll() {
        State state = ALL;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(booker.getId(), page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void testGetItemsBookingsStatePast() {
        State state = PAST;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(booker.getId(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdAndEndIsBefore(anyLong(), any(Instant.class), any());
    }

    @Test
    void testGetItemsBookingsStateFuture() {
        State state = FUTURE;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(booker.getId(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdAndStartIsAfter(anyLong(), any(Instant.class), any());
    }

    @Test
    void testGetItemsBookingsStateCurrent() {
        State state = CURRENT;
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                Instant.now(), Instant.now(), page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), state, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(Instant.class), any(Instant.class), any());
    }

    @Test
    void testGetItemBookingsStateWaiting() {
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStatus(booker.getId(), Status.WAITING, page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), State.WAITING, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void testGetItemBookingsStateRejected() {
        int from = 0;
        int size = 20;
        PageRequest page = PageRequest.of(from / size, size, DEFAULT_SORT);

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findByItemOwnerIdAndStatus(booker.getId(), Status.REJECTED, page))
                .thenReturn(List.of(booking));

        bookingService.getItemsBookings(booker.getId(), State.REJECTED, 0, 2);

        verify(bookingRepository)
                .findByItemOwnerIdAndStatus(anyLong(), any(), any());
    }
}

