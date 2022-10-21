package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemBookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                       @PositiveOrZero @RequestParam(
                                               name = "from", defaultValue = "0") Integer from,
                                       @Positive @RequestParam(
                                               name = "size", defaultValue = "10") Integer size) {
        final ShareitPageRequest pageRequest = new ShareitPageRequest(from, size, Sort.unsorted());
        return itemService.getItems(ownerId, pageRequest);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable Long itemId) throws Exception {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text,
                                @PositiveOrZero @RequestParam(
                                        name = "from", defaultValue = "0") Integer from,
                                @Positive @RequestParam(
                                        name = "size", defaultValue = "10") Integer size) {
        final ShareitPageRequest pageRequest = new ShareitPageRequest(from, size, Sort.unsorted());
        return text.isEmpty() ? Collections.emptyList() : itemService.searchItems(text, pageRequest);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) throws Exception {
        return itemService.addNewItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                    @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemService.addNewComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patch(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                         @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}
