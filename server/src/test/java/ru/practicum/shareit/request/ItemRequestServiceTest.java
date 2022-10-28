package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.ShareitPageRequest;
import ru.practicum.shareit.exception.ServerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    private final LocalDateTime testTime = LocalDateTime.of(
            2022, 01, 01, 00, 00, 00);

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemRequestService itemRequestService;

    private User user;

    private Item item;

    private ItemRequestDto itemRequestDto;

    private ItemRequest itemRequest;

    private ShareitPageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user1@email", "user 1");
        item = new Item(1L, "Item 1", "Item description", true, user, null);
        itemRequestDto = new ItemRequestDto(1L, "request description", user, testTime, List.of(item));
        itemRequest = new ItemRequest(1L, "item request description", user, LocalDateTime.now(), List.of(item));
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestMapper = new ItemRequestMapper();
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRequestMapper);
        pageRequest = new ShareitPageRequest(0, 10, Sort.unsorted());
    }

    @Test
    void addItemRequestTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);

        final ItemRequestDto dto = itemRequestService.addNewItemRequest(1L, itemRequestDto);

        assertNotNull(dto);
        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getRequestor().getName(), user.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());

    }

    @Test
    void addItemRequestWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemRequestService.addNewItemRequest(1L, itemRequestDto);
        });
        assertNotNull(exp);
        assertEquals("Пользователь с таким ID отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getItemRequestByUserIdTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findItemRequestsByRequestor(user))
                .thenReturn(List.of(itemRequest));

        final List<ItemRequestDto> dtos = itemRequestService.getAllItemRequestsByUserId(1L);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(itemRequest.getId(), dtos.get(0).getId());
        assertEquals(user.getName(), dtos.get(0).getRequestor().getName());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findItemRequestsByRequestor(user);

    }

    @Test
    void getItemRequestByUserIdWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemRequestService.getAllItemRequestsByUserId(1L);
        });
        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllItemRequestsTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findItemRequestsByRequestorNot(user, pageRequest))
                .thenReturn(List.of(itemRequest));

        final List<ItemRequestDto> dtos = itemRequestService.getAllItemRequests(1L, pageRequest);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(itemRequest.getId(), dtos.get(0).getId());
        assertEquals(user.getName(), dtos.get(0).getRequestor().getName());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findItemRequestsByRequestorNot(user, pageRequest);

    }

    @Test
    void getAllItemRequestsWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemRequestService.getAllItemRequests(1L, pageRequest);
        });
        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getItemRequestByIdTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(2L))
                .thenReturn(Optional.of(itemRequest));

        final ItemRequestDto dto = itemRequestService.getItemRequestById(1L, 2L);

        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(user.getName(), dto.getRequestor().getName());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(2L);

    }

    @Test
    void getItemRequestByIdWrongUserTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemRequestService.getItemRequestById(1L, 2L);
        });
        assertNotNull(exp);
        assertEquals("Такой User отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getItemRequestByIdWrongItemTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(2L))
                .thenReturn(Optional.empty());
        ServerException exp = assertThrows(ServerException.class, () -> {
            itemRequestService.getItemRequestById(1L, 2L);
        });
        assertNotNull(exp);
        assertEquals("Такой Item Request отсутствует", exp.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(2L);
    }
}