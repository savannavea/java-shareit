package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class ItemRequest {
    Long id;
    @Size(min = 1, max = 200)
    String description;
    User requestor;
    LocalDateTime created;
}