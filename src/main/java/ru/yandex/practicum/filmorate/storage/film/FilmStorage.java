package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getFilms();

    Optional<Film> findFilmById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilmById(Long id);

    void likeFilm(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    Collection<Film> getPopularFilms(Integer count, Integer genreId, LocalDate yearDate);

    Collection<Film> getRecommendations(Long userId, Long otherUserId);

    Collection<Film> getDirectorFilms(String sortBy, Long directorId);

    Collection<Film> getFriendsCommonFilms(Long userId, Long friendId);
}
