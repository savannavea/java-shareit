package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;

    @BeforeEach
    @Test
    void setItemDto() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    @SneakyThrows
    @Test
    void addItem() {
        Long userId = 0L;

        when(itemService.create(userId, itemDto))
                .thenReturn(itemDto);

        String contentAsString = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService)
                .create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Long itemId = 0L;
        Long userId = 0L;

        when(itemService.update(userId, itemDto, itemId))
                .thenReturn(itemDto);

        String contentAsString = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService, times(1))
                .update(userId, itemDto, itemId);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        Long itemId = 0L;
        Long userId = 0L;

        when(itemService.getById(userId, itemId))
                .thenReturn(itemDto);

        String contentAsString = mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService, atLeast(1))
                .getById(userId, itemId);
    }

    @SneakyThrows
    @Test
    void addComment() {
        Long userId = 1L;
        Long itemId = itemDto.getId();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        String contentAsString = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentDto), contentAsString);

        verify(itemService, times(1))
                .addComment(userId, itemId, commentDto);
    }

    @SneakyThrows
    @Test
    void testGetAllByUserId() {
        Long userId = 0L;

        when(itemService.getAllByUserId(userId))
                .thenReturn(List.of(itemDto));

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(("/items"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemDto> result = List.of(itemDto);

        assertEquals(objectMapper.writeValueAsString(result), contentAsString);
        verify(itemService)
                .getAllByUserId(userId);
    }

    @SneakyThrows
    @Test
    void testGetByQuery() {
        String query = "query";

        when(itemService.getByQuery(query))
                .thenReturn(List.of(itemDto));

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.get(("/items/search"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", query)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ItemDto> result = List.of(itemDto);

        assertEquals(objectMapper.writeValueAsString(result), contentAsString);
        verify(itemService)
                .getByQuery(query);
    }

    @SneakyThrows
    @Test
    void testDeleteItem() {
        mockMvc.perform(delete("/items/{itemId}", itemDto.getId()))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .deleteItemsById(itemDto.getId());
    }
}