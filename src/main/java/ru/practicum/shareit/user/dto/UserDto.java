package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;


@Data
@Builder
public class UserDto {
    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;
}
