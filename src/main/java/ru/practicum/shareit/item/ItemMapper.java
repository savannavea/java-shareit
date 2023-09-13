package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable() != null && itemDto.getAvailable())
                .build();

    }

    public static ItemDto toItemDto(Item item) {
    return ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .requestId(item.getItemRequest() == null ? 0 : item.getItemRequest().getId())
            .build();
    }

    public static Item toItemWithOwner(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        item.setItemRequest(item.getItemRequest());
        return item;
    }

    public static ItemDto toItemDto(Item item, ItemOwnerDto lastBooking,
                                    ItemOwnerDto nextBooking, List<CommentDto> commentDtos) {
        ItemDto itemDto = ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDtos)
                .build();
        if (item.getItemRequest() != null) {
            itemDto.setRequestId(item.getItemRequest().getId());
        }
        return itemDto;
    }

    public static List<ItemDto> toItemDtoList(Iterable<Item> items) {
        List<ItemDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemDto(item));
        }
        return result;
    }
}