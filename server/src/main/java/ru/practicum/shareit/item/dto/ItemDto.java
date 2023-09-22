package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 512)
    private String description;

    @NotNull
    private Boolean available;

    private Long ownerId;

    private ItemOwnerDto lastBooking;

    private ItemOwnerDto nextBooking;

    private List<CommentDto> comments;

    @Positive
    private Long requestId;
}
