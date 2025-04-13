package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Optional<Film> findFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film deleteFilmById(Long id);

    Long getNextId();

    void likeFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    Collection<Film> getPopularFilms(Integer count);
}
