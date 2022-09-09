package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final ItemRepository itemRepository;

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getName(), item.getDescription(), item.getAvailable());
    }

}
