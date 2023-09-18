package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User requester;

    @BeforeEach
    @Test
    void setObject() {
        requester = User.builder()
                .id(1L)
                .name("Kate")
                .email("Kate@mail.com")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description of ItemRequestDto")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    @SneakyThrows
    @Test
    void addRequest() {
        Long userId = requester.getId();

        when(itemRequestService.create(userId, itemRequestDto))
                .thenReturn(itemRequestDto);

        String contentAsString = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), contentAsString);
        verify(itemRequestService)
                .create(userId, itemRequestDto);

    }

    @SneakyThrows
    @Test
    void getItemsByUserId() {
        Long userId = requester.getId();

        when(itemRequestService.getAllByUserId(userId))
                .thenReturn(List.of(itemRequestDto));

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(("/requests"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<ItemRequestDto> result = List.of(itemRequestDto);
        assertEquals(objectMapper.writeValueAsString(result), contentAsString);
        verify(itemRequestService)
                .getAllByUserId(userId);
    }

    @SneakyThrows
    @Test
    void returnAll() {
        Long userId = requester.getId();
        int from = 0;
        int size = 20;

        when(itemRequestService.getAllRequests(userId, from, size))
                .thenReturn(List.of(itemRequestDto));

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(("/requests/all"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                                .param("from", String.valueOf(from))
                                .param("size", String.valueOf(size)))
                        //.content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemRequestDto> result = List.of(itemRequestDto);

        assertEquals(objectMapper.writeValueAsString(result), contentAsString);
        verify(itemRequestService)
                .getAllRequests(userId, from, size);

    }

    @SneakyThrows
    @Test
    void get() {
        Long userId = requester.getId();
        Long requestId = itemRequestDto.getId();

        when(itemRequestService.getById(userId, requestId))
                .thenReturn(itemRequestDto);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .get("/requests/{requestId}", requestId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), contentAsString);
        verify(itemRequestService)
                .getById(userId, requestId);
    }
}