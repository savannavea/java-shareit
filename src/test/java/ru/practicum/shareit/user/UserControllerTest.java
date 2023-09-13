package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

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
    MockMvc mvc;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Kate")
                .email("Kate@yandex.ru")
                .build();
    }

    @SneakyThrows
    @Test
    void addUser() {
        UserDto userToCreate = UserDto.builder()
                .id(2L)
                .name("Sofia")
                .email("sofia@yandex.ru")
                .build();
        Mockito
                .when(userService.create(userToCreate))
                .thenReturn(userToCreate);

        String result = mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToCreate)))
                //.andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userToCreate), result);
    }

    @SneakyThrows
    @Test
    void getUsers() {
        List<UserDto> userDtoList = List.of(UserDto.builder()
                .email("@yandex.ru")
                .build());
        Mockito
                .when(userService.getAll())
                .thenReturn(userDtoList);

        String contentAsString = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDtoList), contentAsString);

    }


    @SneakyThrows
    @Test
    void updateUser() {
        UserDto userToUdpate = UserDto.builder()
                .name(null)
                .email("@yandex.ru")
                .build();

        mvc.perform(patch("/users/{userId}", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUdpate)))
                        .andExpect(status().isOk());

//        verify(userService, times(1)).update(userToUdpate,userId);

    }

    @SneakyThrows
    @Test
    void getUserById() {
        mvc.perform(get("/users/{userId}", USER_ID))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito
                .verify(userService)
                .getUserById(USER_ID);
        Mockito
                .verify(userService, times(1))
                .getUserById(USER_ID);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mvc
                .perform(delete("/users/{userId}", USER_ID))
                .andExpect(status().isOk());

        Mockito
                .verify(userService, times(1))
                .deleteUsersById(USER_ID);
    }
}