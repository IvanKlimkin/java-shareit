package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepository {
    private Map<Long, List<Item>> items = new HashMap<>();
    private Long id = 0L;

    public List<Item> getItems(Long ownerId) {
        return items.get(ownerId);
    }

    public Item getItemById(Long itemId) {
        for (List<Item> userItems : items.values()) {
            if (userItems != null) {
                for (Item item : userItems) {
                    if (item.getId().equals(itemId)) {
                        return item;
                    }
                }
            }
        }
        throw new ServerException(String.format("Вещь с Id=%d не найден", itemId));
    }

    public List<Item> searchItems(String text) {
        List<Item> itemContain = new ArrayList<>();
        for (List<Item> userItems : items.values()) {
            itemContain.addAll(userItems.stream()
                    .filter(
                            o -> ((o.getName().toLowerCase().contains(text.toLowerCase()) ||
                                    o.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                                    o.getAvailable().equals(true)))
                    .collect(Collectors.toList()));
        }
        return itemContain;
    }

    public Item addNewItem(Long ownerId, Item item) {
        item.setId(++id);
        if (items.get(ownerId) == null) {
            items.put(ownerId, List.of(item));
        } else items.get(ownerId).add(item);
        return item;
    }

    public void deleteItem(Long ownerId, Long itemId) {
        if (items.containsKey(ownerId)) {
            List<Item> ownerItems = items.get(ownerId);
            ownerItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    public Item updateItem(Long userId, Long id, ItemDto itemDto) {
        for (Item item : items.get(userId)) {
            if (item.getId().equals(id)) {
                if (itemDto.getName() != null) {
                    item.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    item.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    item.setAvailable(itemDto.getAvailable());
                }
                return item;
            }
        }
        return null;
    }
}
