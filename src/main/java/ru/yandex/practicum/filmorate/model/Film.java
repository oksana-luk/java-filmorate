package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    protected Long id;
    @EqualsAndHashCode.Include
    protected String name;
    protected String description;
    @EqualsAndHashCode.Include
    protected LocalDate releaseDate;
    protected Integer duration;
    protected Mpa mpa;
    protected List<Genre> genres = new ArrayList<>();

    public Film() {
    }

    public Film(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
        this.genres = film.getGenres();
        this.mpa = film.getMpa();
    }

    public void addGenre(int genreId, String name) {
        genres.add(new Genre(genreId, name));
    }
}
