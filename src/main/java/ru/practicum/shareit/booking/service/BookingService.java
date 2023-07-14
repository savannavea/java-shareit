package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, String approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, State state, Integer from, Integer size);

    List<BookingDto> getItemsBookings(Long userId, State state, Integer from, Integer size);
}