package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    public static final long USER_ID = 1L;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    User user;

    @BeforeEach
    void setUser() {
        user = User.builder()
                .id(1L)
                .name("Kate")
                .email("Kate@yandex.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void testCreateUser() {
        UserDto userToCreate = UserDto.builder()
                .id(2L)
                .name("Sofia")
                .email("sofia@yandex.ru")
                .build();
        when(userService.create(userToCreate))
                .thenReturn(userToCreate);

        String result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userToCreate), result);
    }

    @SneakyThrows
    @Test
    void testGetUsers() {
        List<UserDto> userDtoList = List.of(UserDto.builder()
                .email("@yandex.ru")
                .build());
        when(userService.getAll())
                .thenReturn(userDtoList);

        String contentAsString = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoList), contentAsString);

    }


    @SneakyThrows
    @Test
    void testUpdateUser() {
        UserDto userToUpdate = UserDto.builder()
                .id(1L)
                .name(null)
                .email("@yandex.ru")
                .build();

        mockMvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .update(userToUpdate, userToUpdate.getId());

    }

    @SneakyThrows
    @Test
    void testGetUserById() {
        mockMvc.perform(get("/users/{userId}", USER_ID))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService)
                .getUserById(USER_ID);
        verify(userService, times(1))
                .getUserById(USER_ID);
    }

    @SneakyThrows
    @Test
    void testDeleteUser() {
        mockMvc.perform(delete("/users/{userId}", USER_ID))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .deleteUsersById(USER_ID);
    }
}