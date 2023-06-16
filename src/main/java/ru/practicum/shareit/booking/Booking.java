package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    @Positive
    Long id;

    @NotNull
    @FutureOrPresent
    LocalDate start;

    @NotNull
    @FutureOrPresent
    LocalDate end;

    @NotNull
    Item item;

    @NotNull
    User booker;

    @NotNull
    Status status;
}
