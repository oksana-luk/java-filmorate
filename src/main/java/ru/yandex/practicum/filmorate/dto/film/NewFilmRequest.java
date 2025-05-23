package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class NewFilmRequest implements BaseFilmDto {
    @NotBlank(message = "Movie title should not be empty")
    private String name;

    @NotNull
    @Size(min = 0, max = 200, message = "Description should be not longer 200 letters.")
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull
    private Integer duration;

    @NotNull
    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();

    private List<Director> directors = new ArrayList<>();
}
