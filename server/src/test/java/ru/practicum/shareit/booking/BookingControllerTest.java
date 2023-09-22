package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private User booker;
    private BookingDto bookingDto;
    private int from;
    private int size;

    @BeforeEach
    @Test
    void setUpBookingDto() {
        User owner = User.builder()
                .id(2L)
                .name("owner")
                .email("email2@email.com")
                .build();

        booker = User.builder()
                .id(1L)
                .name("booker")
                .email("email2@email.com")
                .build();

        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .ownerId(owner.getId())
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusWeeks(1))
                .end(LocalDateTime.now().plusWeeks(2))
                .booker(UserMapper.toUserDto(booker))
                .itemId(1L)
                .item(item)
                .status(Status.APPROVED)
                .build();

        from = 0;
        size = 20;
    }


    @SneakyThrows
    @Test
    void createBookingRequest() {
        when(bookingService.create(bookingDto.getId(), bookingDto))
                .thenReturn(bookingDto);

        String contentAsString = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService)
                .create(booker.getId(), bookingDto);
    }

    @SneakyThrows
    @Test
    void createBookingsWithInvalidDate() {
        when(bookingService.create(bookingDto.getId(), bookingDto))
                .thenReturn(bookingDto);

        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, never())
                .create(bookingDto.getId(), bookingDto);

    }

    @SneakyThrows
    @Test
    void approvedBookingRequest() {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyString()))
                .thenReturn(bookingDto);
        Long bookingId = bookingDto.getId();
        Long userId = booker.getId();


        String contentAsString = mockMvc.perform(MockMvcRequestBuilders
                        .patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", booker.getId())
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService)
                .approve(userId, bookingId, "true");
    }

    @SneakyThrows
    @Test
    void getBooking() {
        Long bookingId = bookingDto.getId();
        Long userId = booker.getId();

        when(bookingService.getById(bookingId, userId))
                .thenReturn(bookingDto);

        String contentAsString = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), contentAsString);
        verify(bookingService, atLeast(1))
                .getById(bookingId, userId);

    }

    @SneakyThrows
    @Test
    void getBookingsOfUser() {
        State state = State.ALL;
        Long userId = booker.getId();
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.getItemsBookings(userId, state, from, size))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1))
                .getUserBookings(userId, state, from, size);

    }

    @SneakyThrows
    @Test
    void getBookingsWithInvalidState() {
        State state = State.ALL;
        Long userId = booker.getId();
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.getItemsBookings(userId, state, from, size))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALLLL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, never())
                .getItemsBookings(userId, state, from, size);

    }

    @SneakyThrows
    @Test
    void getBookingByItemOwner() {

        State state = State.ALL;
        Long userId = booker.getId();
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.getUserBookings(userId, state, from, size))
                .thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1))
                .getItemsBookings(userId, state, from, size);

    }
}