package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {

        Booking booking = BookingMapper.toBooking(bookingDto);
        User user = getUserOrElseThrow(userId);
        booking.setBooker(user);
        Item item = getItemOrElseThrow(bookingDto.getItemId());
        booking.setItem(item);

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item is not available");
        }
        if (item.getOwner().getId().equals(booking.getBooker().getId())) {
            throw new NotFoundException("Бронирование не найдено");
        }
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, String approved) {

        getUserOrElseThrow(userId);
        Booking booking = getBookingOrElseThrow(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Id of the user's item does not match the id of the owner of the item");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("The status has been confirmed by the owner before");
        }

        try {
            boolean isApprove = Boolean.parseBoolean(approved);
            if (isApprove) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            booking = bookingRepository.save(booking);
        } catch (Exception e) {
            throw new BadRequestException("Invalid approve parameter");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {

        Booking booking = getBookingOrElseThrow(bookingId);

        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("This user cannot view booking information");
        }
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, State state) {
        getUserOrElseThrow(userId);
        List<Booking> bookings = bookingRepository.findByBooker_Id(userId);

        Instant time = Instant.now();

        switch (state) {
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, time, SORT);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, time, SORT);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                        time, time, SORT);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, SORT);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, SORT);
                break;
            default:
                bookings.sort((booking1, booking2) -> booking2.getStart().compareTo(booking1.getStart()));
                break;
        }

        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            BookingDto bookingDto = BookingMapper.toBookingDto(booking);
            bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
            bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
            result.add(bookingDto);
        }

        return result;
    }

    @Override
    public List<BookingDto> getItemsBookings(Long userId, State state) {

        getUserOrElseThrow(userId);
        List<Booking> bookings;
        Instant time = Instant.now();

        switch (state) {
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, time, SORT);
                break;
            case FUTURE:

                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, time, SORT);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        time, time, SORT);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING, SORT);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED, SORT);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }

        List<BookingDto> result = new ArrayList<>();

        for (Booking booking : bookings) {
            var bookingDto = BookingMapper.toBookingDto(booking);
            bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
            bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
            result.add(bookingDto);
        }

        return result;
    }

    private User getUserOrElseThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", userId)));
    }

    private Item getItemOrElseThrow(Long itemId) {
        return itemRepository
                .findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item's id %d doesn't found!", itemId)));
    }

    private Booking getBookingOrElseThrow(Long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Item's id %d doesn't found!", bookingId)));
    }
}