package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto bookingDto) {
        validateDate(bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId,
                              @RequestParam String approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, validateState(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getItemsBookings(userId, validateState(state));
    }

    private static void validateDate(BookingDto bookingDto) {
        final LocalDateTime start = bookingDto.getStart();
        final LocalDateTime end = bookingDto.getEnd();

        if (start == null || end == null
                || start.isAfter(end)
                || start.equals(end)
                || start.isBefore(LocalDateTime.now())
        ) {
            throw new BadRequestException("Invalid booking dates");
        }
    }

    private State validateState(String state) {
        State stateEnum;
        try {
            stateEnum = State.valueOf(state);

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return stateEnum;
    }
}