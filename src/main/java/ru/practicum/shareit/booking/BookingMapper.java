package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

import java.time.*;

public class BookingMapper {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();
    public static BookingDto toBookingDto(Booking booking) {
        LocalDateTime start = LocalDateTime.ofInstant(booking.getStart(), ZONE_ID);
        LocalDateTime end = LocalDateTime.ofInstant(booking.getEnd(), ZONE_ID);

        return BookingDto.builder()
                .id(booking.getId())
                .start(start)
                .end(end)
                .status(booking.getStatus())
                .itemId(booking.getItem().getId())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Instant start = bookingDto.getStart().toInstant(ZONE_OFFSET);
        Instant end = bookingDto.getEnd().toInstant(ZONE_OFFSET);

        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(start);
        booking.setEnd(end);

        Item item = new Item();
        item.setId(bookingDto.getItemId());

        booking.setItem(item);
        if (bookingDto.getStatus() != null) {
            Status status = bookingDto.getStatus();
            booking.setStatus(status);
        }
        return booking;
    }

    public static ItemOwnerDto toBookingItemDto(Booking booking) {
        LocalDateTime start = LocalDateTime.ofInstant(booking.getStart(), ZONE_ID);
        LocalDateTime end = LocalDateTime.ofInstant(booking.getEnd(), ZONE_ID);
        return ItemOwnerDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(start)
                .end(end)
                .build();
    }
}