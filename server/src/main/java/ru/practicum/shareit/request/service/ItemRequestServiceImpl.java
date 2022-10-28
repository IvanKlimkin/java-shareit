package ru.practicum.shareit.request.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto addNewItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Пользователь с таким ID отсутствует"));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllItemRequestsByUserId(Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestRepository.findItemRequestsByRequestor(requestor)) {
            listItemRequestDto.add(itemRequestMapper.toDto(itemRequest));
        }
        return listItemRequestDto.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllItemRequests(Long userId, ShareitPageRequest pageRequest) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Такой User отсутствует"));
        List<ItemRequestDto> listItemRequestDto = itemRequestMapper.toDto(
                itemRequestRepository.findItemRequestsByRequestorNot(requestor, pageRequest));
        return listItemRequestDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long itemRequestId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new ServerException("Такой User отсутствует"));
        return itemRequestMapper.toDto(itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new ServerException("Такой Item Request отсутствует")));
    }

}
