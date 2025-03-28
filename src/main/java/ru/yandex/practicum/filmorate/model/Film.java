package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @NotNull(groups = UpdateInfo.class, message = "Id of user should not be empty")
    protected Long id;

    @NotBlank(groups = CreateInfo.class, message = "Movie title should not be empty")
    @EqualsAndHashCode.Include
    protected String name;

    @NotNull(groups = CreateInfo.class)
    @Size(groups = {UpdateInfo.class, CreateInfo.class}, min = 0, max = 200, message = "Description should be not longer 200 letters.")
    protected String description;

    @NotNull(groups = CreateInfo.class)
    @EqualsAndHashCode.Include
    protected LocalDate releaseDate;

    @NotNull(groups = CreateInfo.class)
    protected Duration duration;
}
