package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @Past(groups = {CreateInfo.class, UpdateInfo.class}, message = "Birthday cannot be a future date")
    protected LocalDate birthday;

    protected String name;
}
