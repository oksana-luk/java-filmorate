package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.getFilms();
    }

    public Film findFilmById(Long id) {
        return validateNotFound(id);
    }

    public Film createFilm(Film film) {
        validate(film);
        film.setId(filmStorage.getNextId());
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        validateNotFound(film.getId());
        validate(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film partialUpdate(Long id, Map<String, Object> updates) {
        Film currentFilm = validateNotFound(id);
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Film.class, key);
            if (field != null && !field.getName().equals("id")) {
                field.setAccessible(true);
                if (field.getName().equals("releaseDate")) {
                    LocalDate releaseDate = LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    ReflectionUtils.setField(field, currentFilm, releaseDate);
                } else {
                    ReflectionUtils.setField(field, currentFilm, value);
                }
            }
        });
        validateAll(currentFilm);
        filmStorage.updateFilm(currentFilm);
        return currentFilm;
    }

    public Film deleteFilm(Long id) {
        validateNotFound(id);
        return filmStorage.deleteFilmById(id);
    }

    public void likeFilm(Long id, Long userId) {
        validateNotFound(id);
        validateNotFoundUser(userId);
        filmStorage.likeFilm(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        validateNotFound(id);
        validateNotFoundUser(userId);
        filmStorage.deleteLike(id, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        validationCount(count);
        return filmStorage.getPopularFilms(count);
    }

    private void validate(Film film) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            setLogWarn(String.format("The minimum release date should be December 28, 1895. Current release date is %s", film.getReleaseDate()));
            throw new ValidationException(film.getReleaseDate().toString(), "The minimum release date should be December 28, 1895");
        }
        if (film.getDuration() < 0) {
            setLogWarn(String.format("The duration of the movie should be a positive number or 0. Current duration is %s", film.getDuration()));
            throw new ValidationException(film.getDuration().toString(), "The duration of the movie should be a positive number");
        }
        setLogValidationSuccess(film);
    }

    private void validateAll(Film film) {
        if (film.getName() != null && film.getName().isBlank()) {
            setLogWarn("Movie title should not be empty");
            throw new ValidationException(film.getName(), "Movie title should not be empty");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            setLogWarn(String.format("The maximum description length of 200 characters has been exceeded. Current description is: %s", film.getDescription()));
            throw new ValidationException(film.getDescription(), "The maximum description length of 200 characters has been exceeded");
        }
        if (film.getReleaseDate() != null && !film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            setLogWarn(String.format("The minimum release date should be December 28, 1895. Current release date is %s", film.getReleaseDate()));
            throw new ValidationException(film.getReleaseDate().toString(), "The minimum release date should be December 28, 1895");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            setLogWarn(String.format("The duration of the movie should be a positive number. Current duration is %s", film.getDuration()));
            throw new ValidationException(film.getDuration().toString(), "The duration of the movie should be a positive number");
        }
        setLogValidationSuccess(film);
    }

    private Film validateNotFound(Long id) {
        Optional<Film> filmOpt = filmStorage.findFilmById(id);
        if (filmOpt.isEmpty()) {
            String message = String.format("The service did not find a movie by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        } else {
            return filmOpt.get();
        }
    }

    private void validateNotFoundUser(Long id) {
        if (userStorage.findUserById(id).isEmpty()) {
            String message = String.format("The service did not find user by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        }
    }

    private void validationCount(Integer count) {
        if (count <= 0) {
            setLogWarn(String.format("The count of the movies should be a positive number. Current count is %s", count));
            throw new ValidationException(count.toString(), "The count of the movies should be a positive number");
        }
    }

    private void setLogWarn(String message) {
        log.warn("The process ended with an error. {}", message);
    }

    private void setLogValidationSuccess(Film film) {
        log.debug("The validation process for movie {} was completed successfully.", film.getName());
    }
}

