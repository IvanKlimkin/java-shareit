package ru.practicum.shareit.item.service;

import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemBookingDto> getItems(Long ownerId, MyPageRequest pageRequest);

    ItemBookingDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchItems(String text, MyPageRequest pageRequest);

    ItemDto addNewItem(Long ownerId, ItemDto itemDto);

    CommentDto addNewComment(Long userId, Long itemId, CommentDto commentDto);

    void deleteItem(Long ownerId, Long itemId);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);
}
