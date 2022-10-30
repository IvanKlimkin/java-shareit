package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                         @PositiveOrZero @RequestParam(
                                                 name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(
                                                 name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable Long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(
                                                 name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(
                                                 name = "size", defaultValue = "10") Integer size) {
        log.info("Search items with text {}", text);
        return text.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : itemClient.searchItems(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Add item with name:{}, userId={}", itemDto.getName(), userId);
        return itemClient.addNewItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                                @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        log.info("Add new comment to ItemId={}, userId={}", itemId, userId);
        return itemClient.addNewComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                                        @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Update Item with Id={}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId) {
        log.info("Delete item by Id={}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }
}
