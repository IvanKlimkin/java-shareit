package ru.practicum.shareit.request.service;

import ru.practicum.shareit.MyPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllItemRequestsByUserId(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId, MyPageRequest pageRequest);

    ItemRequestDto getItemRequestById(Long userId, Long itemRequestId);
}
