package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static CommentDto toCommentDto(Comment comment) {
        LocalDateTime created = LocalDateTime.ofInstant(comment.getCreated(), ZONE_OFFSET);
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(created)
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .build();
    }

    public static List<CommentDto> toICommentDtoList(Iterable<Comment> comments) {
        List<CommentDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toCommentDto(comment));
        }
        return result;
    }
}
