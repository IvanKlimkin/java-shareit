package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long ownerId);

    Item getItemById(Long itemId);

    List<Item> searchItems(String text);

    Item addNewItem(Long ownerId, Item item);

    void deleteItem(Long ownerId, Long itemId);

    Item updateItem(Long userId, Long id, ItemDto itemDto);
}
