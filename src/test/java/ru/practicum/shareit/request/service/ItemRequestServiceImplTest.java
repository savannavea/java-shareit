package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private User owner;
    private User requester;
    private ItemDto itemDto;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUser() {
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("email2@email.com")
                .build();

        requester = User.builder()
                .id(1L)
                .name("requester")
                .email("email2@email.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .ownerId(owner.getId())
                .available(true)
                .requestId(requester.getId())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description ItemRequestDto")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testCrestedItemRequestDto() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.save(ItemRequestMapper.toItemRequest(owner, itemRequestDto)))
                .thenReturn(ItemRequestMapper.toItemRequest(owner, itemRequestDto));

        ItemRequestDto result = itemRequestService.create(owner.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());

        verify(itemRequestRepository)
                .save(any());
    }

    @Test
    void testGetAllByUserId() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findItemRequestByRequesterId(owner.getId()))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(owner, itemRequestDto)));
        when(itemRepository.findByItemRequestId(itemRequestDto.getId()))
                .thenReturn(List.of(ItemMapper.toItem(itemDto)));

        List<ItemRequestDto> result = itemRequestService.getAllByUserId(owner.getId());

        Assertions.assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllRequests() {
        Long userId = requester.getId();
        int from = 0;
        int size = 20;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(requester));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedAsc(userId, PageRequest.of(from, size)))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(requester, itemRequestDto)));
        when(itemRepository.findByItemRequestId(itemRequestDto.getId()))
                .thenReturn(List.of(ItemMapper.toItem(itemDto)));

        List<ItemRequestDto> actualRequestsDto = itemRequestService.getAllRequests(userId, from, size);

        assertNotNull(actualRequestsDto);
    }

    @Test
    void testGetItemRequestById() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requester.getId()))
                .thenReturn(Optional.ofNullable(ItemRequestMapper.toItemRequest(requester, itemRequestDto)));
        when(itemRepository.findByItemRequestId(itemRequestDto.getId()))
                .thenReturn(List.of(ItemMapper.toItem(itemDto)));

        ItemRequestDto returnedDto = itemRequestService.getById(owner.getId(), itemRequestDto.getId());

        assertThat(returnedDto, notNullValue());
        assertThat(returnedDto.getId(), equalTo(itemRequestDto.getId()));
        verify(itemRequestRepository)
                .findById(itemRequestDto.getId());

    }
}