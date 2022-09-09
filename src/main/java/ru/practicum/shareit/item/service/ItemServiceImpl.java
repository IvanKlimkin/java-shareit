package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    public List<Item> getItems(Long ownerId) {
        return itemRepository.getItems(ownerId);
    }

    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public Item addNewItem(Long ownerId, Item item) {
        return itemRepository.addNewItem(ownerId, item);
    }

    @Override
    public void deleteItem(Long ownerId, Long itemId) {
        itemRepository.deleteItem(ownerId, itemId);
    }

    @Override
    public Item updateItem(Long userId, Long id, ItemDto itemDto) {
        if (itemRepository.getItems(userId) != null) {
            List<Long> userItemsId = itemRepository.getItems(userId).stream().map(Item::getId).collect(Collectors.toList());
            if (userItemsId.contains(id)) {
                return itemRepository.updateItem(userId, id, itemDto);
            }
        }
        throw new ServerException("Только владелец вещи может менять параметры");

    }
}
