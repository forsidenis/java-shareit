package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.format.DateTimeFormatter;

public class CommentMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().format(FORMATTER))
                .build();
    }

    public static Comment toComment(String text, Item item, User author) {
        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(java.time.LocalDateTime.now())
                .build();
    }
}
