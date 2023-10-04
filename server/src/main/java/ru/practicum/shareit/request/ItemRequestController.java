package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor__ = @Autowired)
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.ok(itemRequestService.create(userId, itemRequestDto));
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByUserId(userId);

    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "from", required = false, defaultValue = "0")
                                                Integer from,
                                                @RequestParam(value = "size", required = false, defaultValue = "10")
                                                Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);

    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable("requestId") Long requestId) {
        return itemRequestService.getById(userId, requestId);

    }

}