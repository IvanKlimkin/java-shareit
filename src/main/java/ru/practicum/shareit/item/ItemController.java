package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;


    @GetMapping
    public List<Item> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Long itemId) throws Exception {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        return text.isEmpty() ? Collections.emptyList() : itemService.searchItems(text);
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @RequestBody @Valid Item item) throws Exception {
        userService.getUserById(userId);
        itemService.addNewItem(userId, item);
        return item;
    }

    @PatchMapping("/{itemId}")
    public Item patch(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                      @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
