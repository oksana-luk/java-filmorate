package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.debug("GET /films: the collection of movies has been returned");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findFilmById(@PathVariable Long id) {
        log.debug("GET/films/id: start of finding of movie {}", id);
        FilmDto filmDto = filmService.findFilmById(id);
        log.debug("GET/films/id: the process was completed successfully. A new movie {} with id {} has been found", filmDto.getName(), filmDto.getId());
        return filmDto;
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody NewFilmRequest newFilmRequest) {
        log.debug("POST/films: start of creating of new movie {}", newFilmRequest.getName());
        FilmDto filmDto = filmService.createFilm(newFilmRequest);
        log.debug("POST/films: the process was completed successfully. A new movie {} with id {} has been created", filmDto.getName(), filmDto.getId());
        return filmDto;
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequest updateFilmRequest) {
        log.debug("PUT/films: start of updating of movie {}", updateFilmRequest.getName());
        FilmDto filmDto = filmService.updateFilm(updateFilmRequest);
        log.debug("PUT/films: the process was completed successfully. A movie {} with id {} has been updated", updateFilmRequest.getName(), updateFilmRequest.getId());
        return filmDto;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFilm(@PathVariable Long id) {
        log.debug("DELETE/films/id: start of deleting of film with id {}", id);
        filmService.deleteFilm(id);
        log.debug("DELETE/films/id: the process was completed successfully. A film with id {} has been deleted", id);
        return ResponseEntity.ok(Map.of("result",String.format("Film with id %s has been deleted successfully.", id)));
    }

    @PutMapping("{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("PUT/films/id/like/id: start of liking of movie with id {}", id);
        filmService.likeFilm(id, userId);
        log.debug("PUT/films/id/like/id: the process was completed successfully. A movie with id {} has been liked of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("A movie with id %d has an like from user with id %d", id, userId)));
    }

    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<Map<String, String>> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("DELETE/films/id/like/id: start of deleting like of movie with id {}", id);
        filmService.deleteLike(id, userId);
        log.debug("DELETE/films/id/like/id: the process was completed successfully.  From movie with id {} has been deleted like of user with id {}", id, userId);
        return ResponseEntity.ok(Map.of("result", String.format("From movie with id %d has been deleted an like from user with id %d", id, userId)));
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        count = (count == null) ? 10 : count;
        log.debug("GET/films/popular: start of finding {} popular movie", count);
        Collection<Film> films = filmService.getPopularFilms(count);
        log.debug("GET/films/process: the process was completed successfully. The collection of {} popular movies has been returned", count);
        return films;
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilmsFriends(@RequestParam Long userId, @RequestParam Long friendId) {
        log.debug("GET/films/common: start of finding {} common movie");
        Collection<Film> films = filmService.getCommonFilmsFriends(userId, friendId);
        log.debug("GET/films/process: the process was completed successfully. The collection of {} common movies has been returned");
        return films;
    }
}
