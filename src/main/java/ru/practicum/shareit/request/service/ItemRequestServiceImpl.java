package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUserOrElseThrow(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        getUserOrElseThrow(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findItemRequestByRequesterId(userId);
        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        getUserOrElseThrow(userId);
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequesterIdNotOrderByCreatedAsc(userId, PageRequest.of(from, size));

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(addItemsToRequest(itemRequest));
        }
        return result;

    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserOrElseThrow(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", userId)));
        addItemsToRequest(itemRequest);
        return addItemsToRequest(itemRequest);
    }

    private User getUserOrElseThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User's id %d doesn't found!", userId)));
    }

    private ItemRequestDto addItemsToRequest(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        List<Item> items = itemRepository.findByItemRequestId(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.toItemDtoList(items));

        return itemRequestDto;
    }

}
