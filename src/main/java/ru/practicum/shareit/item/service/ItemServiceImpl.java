package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemBookingDto> getItems(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<ItemBookingDto> listBookingItems = new ArrayList<>();
        for (Item item : itemRepository.findItemsByOwner(owner)) {
            listBookingItems.add(getItemWithBooking(item, ownerId));
        }
        return listBookingItems.stream().sorted(Comparator.comparing(ItemBookingDto::getId)).collect(Collectors.toList());
    }

    private ItemBookingDto getItemWithBooking(Item item, Long userId) {
        Booking nextBooking = bookingRepository.findBookingByItemAndStartAfter(
                item, LocalDateTime.now(), Sort.by("start"));
        Booking lastBooking = bookingRepository.findBookingByItemAndEndBefore(
                item, LocalDateTime.now(), Sort.by(Sort.Order.desc("end")));

        ItemBookingDto itemBookingDto = new ItemBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                (lastBooking == null || !(item.getOwner().getId().equals(userId))) ?
                        null : new BookingInfo(lastBooking.getId(), lastBooking.getBooker().getId()),
                (nextBooking == null || !(item.getOwner().getId().equals(userId))) ?
                        null : new BookingInfo(nextBooking.getId(), nextBooking.getBooker().getId()),
                commentMapper.toDto(commentRepository.findCommentsByItem(item)));
        return itemBookingDto;
    }

    public ItemBookingDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(
                itemId).orElseThrow(() -> new ServerException("Вещь с таким ID отсутствует."));
        return getItemWithBooking(item, userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemMapper.toDto(itemRepository.search(text));
    }

    @Override
    @Transactional
    public ItemDto addNewItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ServerException("Пользователь с таким ID или сущностью отсутствуют"));
        Item item = itemMapper.toItem(itemDto, owner);
        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto addNewComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Пользователь с таким ID отсутствует"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ServerException("Сущность с таким ID отсутствует"));
        Booking booking = bookingRepository.findBookingByBookerAndItemAndStatusAndEndIsBefore(
                author, item, Status.APPROVED, LocalDateTime.now());
        if (booking != null) {
            return commentMapper.toDto(commentRepository.save(commentMapper.toComment(commentDto, item, author)));
        } else throw new ValidationException("Только пользователь закончивший аренду может оставлять отзывы.");
    }

    @Override
    @Transactional
    public void deleteItem(Long ownerId, Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Пользователь с таким ID или сущностью отсутствуют"));
        Item item = itemRepository.findItemByIdAndOwner(id, user);
        if (item != null) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
                  }
            itemRepository.save(item);
            return itemMapper.toDto(item);
        }
        else {
            throw new ServerException("Сущность с таким ID отсутствуют");
        }
    }
}
