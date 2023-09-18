package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CommentDto {

    private Long id;

    @NotBlank
    @Size(max = 500)
    private String text;

    private String authorName;

    private LocalDateTime created;
}