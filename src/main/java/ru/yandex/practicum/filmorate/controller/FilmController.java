package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.CreateInfo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UpdateInfo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("GET /films: the collection of movies were return");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Validated(CreateInfo.class) @RequestBody Film film) {
        String method = "POST /films";
        validate(film, method);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("{}: the process was completed successfully. A new movie {} with id {} was created", method, film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Validated({UpdateInfo.class, CreateInfo.class}) @RequestBody Film film) {
        String method = "PUT /films";
        if (!films.containsKey(film.getId())) {
            String message = String.format("The service did not find a movie by id %s", film.getId());
            setLogWarn(method, message);
            throw new NotFoundException(message);
        }
        validate(film, method);
        films.put(film.getId(), film);
        log.debug("{}: the process was completed successfully. A movie {} with id {} was updated", method, film.getName(), film.getId());
        return film;
    }

    @PatchMapping
    public Film partialUpdate(@Validated(UpdateInfo.class) @RequestBody Film film) {
        String method = "PATCH /films";
        if (!films.containsKey(film.getId())) {
            String message = String.format("The service did not find a movie by id %s", film.getId());
            setLogWarn(method, message);
            throw new NotFoundException(message);
        }
        Film currentFilm = films.get(film.getId());
        validateAll(film, method);
        partialUpdate(film, currentFilm);
        films.put(currentFilm.getId(), currentFilm);
        log.debug("{}: the process was completed successfully. A movie {} with id {} was partial updated", method, currentFilm.getName(), currentFilm.getId());
        return currentFilm;
    }

    private void partialUpdate(Film sourceFilm, Film receivefilm) {
        if (!Objects.isNull(sourceFilm.getName())) {
            receivefilm.setName(sourceFilm.getName());
        }
        if (!Objects.isNull(sourceFilm.getDescription())) {
            receivefilm.setDescription(sourceFilm.getDescription());
        }
        if (!Objects.isNull(sourceFilm.getReleaseDate())) {
            receivefilm.setReleaseDate(sourceFilm.getReleaseDate());
        }
        if (!Objects.isNull(sourceFilm.getDuration())) {
            receivefilm.setDuration(sourceFilm.getDuration());
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateAll(Film film, String method) {
        if (film.getName() != null && film.getName().isBlank()) {
            setLogWarn(method, "Movie title should not be empty");
            throw new ValidationException("Movie title should not be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            setLogWarn(method, String.format("The maximum description length of 200 characters has been exceeded. Current description is: %s", film.getDescription()));
            throw new ValidationException("The maximum description length of 200 characters has been exceeded");
        }
        if (film.getReleaseDate() != null && !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            setLogWarn(method, String.format("The minimum release date should be December 28, 1895. Current release date is %s", film.getReleaseDate()));
            throw new ValidationException("The minimum release date should be December 28, 1895");
        }
        if (film.getDuration() != null && film.getDuration().isNegative()) {
            setLogWarn(method, String.format("The duration of the movie should be a positive number. Current duration is %s", film.getDuration()));
            throw new ValidationException("The duration of the movie should be a positive number");
        }
    }

    private void validate(Film film, String method) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            setLogWarn(method, String.format("The minimum release date should be December 28, 1895. Current release date is %s", film.getReleaseDate()));
            throw new ValidationException("The minimum release date should be December 28, 1895");
        }
        if (film.getDuration().isNegative()) {
            setLogWarn(method, String.format("The duration of the movie should be a positive number. Current duration is %s", film.getDuration()));
            throw new ValidationException("The duration of the movie should be a positive number");
        }
    }

    private void setLogWarn(String method, String message) {
        log.warn("{}: the process ended with an error. {}", method, message);
    }
}
