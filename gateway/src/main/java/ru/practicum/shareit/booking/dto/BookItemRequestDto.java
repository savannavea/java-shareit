package ru.practicum.shareit.booking.dto;

import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
    @NotNull(message = "Не указана вещь")
    private long itemId;
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования не может быть в прошлом")
    private LocalDateTime end;
}
