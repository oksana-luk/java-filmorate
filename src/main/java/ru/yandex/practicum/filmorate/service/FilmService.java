package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.dto.film.BaseFilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final DirectorStorage directorStorage;
    private final FeedService feedService;

    @Autowired
    public FilmService(@Qualifier("filmRepository") FilmStorage filmStorage,
                       @Qualifier("userRepository") UserStorage userStorage,
                       @Qualifier("directorRepository") DirectorStorage directorStorage,
                       GenreRepository genreRepository, MpaRepository mpaRepository, FeedService feedService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.directorStorage = directorStorage;
        this.feedService = feedService;
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.getFilms()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto findFilmById(Long id) {
        Film film = validateNotFound(id);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto createFilm(NewFilmRequest newFilmRequest) {
        validateReleaseDate(newFilmRequest);
        validateDuration(newFilmRequest);
        validateGenre(newFilmRequest.getGenres());
        validateRating(newFilmRequest.getMpa());
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        film = filmStorage.addFilm(film);
        film = validateNotFound(film.getId());
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest updateFilmRequest) {
        validateNotFound(updateFilmRequest.getId());
        if (updateFilmRequest.hasReleaseDate()) {
            validateReleaseDate(updateFilmRequest);
        }
        if (updateFilmRequest.hasDuration()) {
            validateDuration(updateFilmRequest);
        }
        if (updateFilmRequest.hasGenres()) {
            validateGenre(updateFilmRequest.getGenres());
        }
        if (updateFilmRequest.hasRating()) {
            validateRating(updateFilmRequest.getMpa());
        }
        Film film = FilmMapper.updateFilmFields(updateFilmRequest);
        film = filmStorage.updateFilm(film);
        film = validateNotFound(film.getId());
        return FilmMapper.mapToFilmDto(film);
    }

    public boolean deleteFilm(Long id) {
        validateNotFound(id);
        return filmStorage.deleteFilmById(id);
    }

    public void likeFilm(Long id, Long userId) {
        validateNotFound(id);
        validateNotFoundUser(userId);
        filmStorage.likeFilm(id, userId);
        feedService.addEvent(userId, EventType.LIKE, EventOperation.ADD, id);
        log.debug("Событие добавлено в ленту: пользователь с id: {} лайкнул фильм с id: {}", userId, id);
    }

    public void deleteLike(Long id, Long userId) {
        validateNotFound(id);
        validateNotFoundUser(userId);
        filmStorage.deleteLike(id, userId);
        feedService.addEvent(userId, EventType.LIKE, EventOperation.REMOVE, id);
        log.debug("Событие добавлено в ленту: пользователь с id: {} удалил лайк у фильма с id: {}", userId, id);
    }

    public Collection<FilmDto> getPopularFilms(Integer count, Integer genreId, LocalDate yearDate) {
        validationCount(count);
        if (!Objects.isNull(genreId)) {
            genreRepository.findGenreById(genreId).orElseThrow(() -> {
                String message = String.format("The service did not find genre by id %s", genreId);
                setLogWarn(message);
                return new NotFoundException(message);
            });
        }
        return filmStorage.getPopularFilms(count, genreId, yearDate)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getDirectorFilms(String sortBy, Long directorId) {
        validateDirector(directorId);
        return filmStorage.getDirectorFilms(sortBy, directorId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateDirector(Long id) {
        directorStorage.findDirectorById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Директор " + id + " не найден");
            log.error(e.getMessage());
            return e;
        });
    }

    private void validateDuration(BaseFilmDto film) {
        if (film.getDuration() < 0) {
            setLogWarn(String.format("The duration of the movie should be a positive number or 0. Current duration is %s", film.getDuration()));
            throw new ValidationException(film.getDuration().toString(), "The duration of the movie should be a positive number");
        }
        setLogValidationSuccess(film);
    }

    private void validateReleaseDate(BaseFilmDto film) {
        if (!film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28))) {
            setLogWarn(String.format("The minimum release date should be December 28, 1895. Current release date is %s", film.getReleaseDate()));
            throw new ValidationException(film.getReleaseDate().toString(), "The minimum release date should be December 28, 1895");
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

    private void validateGenre(List<Genre> genres) {
        if (genreRepository == null || genres.isEmpty()) {
            return;
        }
        for (Genre genre : genres) {
            Optional<GenreDto> genreOpt = genreRepository.findGenreById(genre.getId());

            if (genreOpt.isEmpty()) {
                String message = String.format("The service did not find genre by id %s", genre.getId());
                setLogWarn(message);
                throw new NotFoundException(message);
            }
        }
    }

    private void validateRating(Mpa mpa) {
        if (Objects.isNull(mpa) || mpaRepository == null) {
            return;
        }
        Optional<MpaDto> mpaOpt = mpaRepository.findMpaById(mpa.getId());
        if (mpaOpt.isEmpty()) {
            String message = String.format("The service did not find rating by id %s", mpa.getId());
            setLogWarn(message);
            throw new NotFoundException(message);
        }
    }

    private void setLogWarn(String message) {
        log.warn("The process ended with an error. {}", message);
    }

    private void setLogValidationSuccess(BaseFilmDto film) {
        log.debug("The validation process for movie {} was completed successfully.", film.getName());
    }

    public Collection<FilmDto> getFriendsCommonFilms(Long userId, Long friendId) {
        validateNotFoundUser(userId);
        validateNotFoundUser(friendId);
        return filmStorage.getFriendsCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> searchFilmsByString(String query, String sortBy) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        Collection<Film> films;
        Set<String> fields = new HashSet<>(Arrays.asList(sortBy.split(",")));
        boolean searchByTitle = fields.contains("title");
        boolean searchByDirector = fields.contains("director");

        if (searchByTitle && searchByDirector) {
            films = filmStorage.searchByTitleAndDirector(query);
        } else if (searchByTitle) {
            films = filmStorage.searchByTitle(query);
        } else if (searchByDirector) {
            films = filmStorage.searchByDirector(query);
        } else {
            return Collections.emptyList();
        }
        return films
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}