package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateUserRequest implements BaseUserDto {
    @NotNull(message = "Id of user should not be empty")
    private Long id;

    @Email(message = "Invalid email format")
    private String email;
    private String login;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birthday cannot be a future date")
    private LocalDate birthday;

    private String name;

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasBirthday() {
        return birthday != null;
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
}
