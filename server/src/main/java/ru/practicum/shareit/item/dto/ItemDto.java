package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long ownerId;

    private ItemOwnerDto lastBooking;

    private ItemOwnerDto nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}
