package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class UpdateFilmRequest implements BaseFilmDto {
    @NotNull
    private Long id;

    private String name;

    @Size(min = 0, max = 200, message = "Description should be not longer 200 letters.")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private Integer duration;

    private Mpa mpa;

    private List<Genre> genres = new LinkedList<>();
    private List<Director> directors = new LinkedList<>();

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return ! (duration == null || duration == 0);
    }

    public boolean hasRating() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return ! genres.isEmpty();
    }

    public boolean hasDirectors() {
        return !directors.isEmpty();
    }
}
