package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @NotNull(groups = UpdateInfo.class, message = "Id of user should not be empty")
    protected Long id;

    @NotNull(groups = CreateInfo.class)
    @Email(groups = {CreateInfo.class, UpdateInfo.class}, message = "Invalid email format")
    @EqualsAndHashCode.Include
    protected String email;

    @NotBlank(groups = CreateInfo.class, message = "Login should not be empty")
    @EqualsAndHashCode.Include
    protected String login;

    @NotNull(groups = CreateInfo.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(groups = {CreateInfo.class, UpdateInfo.class}, message = "Birthday cannot be a future date")
    protected LocalDate birthday;

    protected String name;

    public User() {

    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.birthday = user.getBirthday();
        this.name = user.getName();
    }
}
