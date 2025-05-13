package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class NewUserRequest implements BaseUserDto {
    @NotNull
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Login should not be empty")
    private String login;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birthday cannot be a future date")
    private LocalDate birthday;
    private String name;
}
