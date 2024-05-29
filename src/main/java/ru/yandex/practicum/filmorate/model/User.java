package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private Long id;
    @Email
    @NonNull
    @NotBlank
    private String email;

    @NonNull
    @NotBlank
    private String login;
    private String name;

    @Past
    private LocalDate birthday;
}
