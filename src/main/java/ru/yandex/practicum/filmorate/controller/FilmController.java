package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.CreateInfo;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UpdateInfo;
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
    public Collection<Film> findAll() {
        log.debug("GET /films: the collection of movies has been returned");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Long id) {
        log.debug("GET/films/id: start of finding of movie {}", id);
        Film film = filmService.findFilmById(id);
        log.debug("GET/films/id: the process was completed successfully. A new movie {} with id {} has been found", film.getName(), film.getId());
        return film;
    }

    @PostMapping
    public Film createFilm(@Validated(CreateInfo.class) @RequestBody Film film) {
        log.debug("POST/films: start of creating of new movie {}", film.getName());
        filmService.createFilm(film);
        log.debug("POST/films: the process was completed successfully. A new movie {} with id {} has been created", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Validated({UpdateInfo.class, CreateInfo.class}) @RequestBody Film film) {
        log.debug("PUT/films: start of updating of movie {}", film.getName());
        filmService.updateFilm(film);
        log.debug("PUT/films: the process was completed successfully. A movie {} with id {} has been updated", film.getName(), film.getId());
        return film;
    }

    @PatchMapping("/{id}")
    public Film partialUpdate(@Validated(UpdateInfo.class) @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        log.debug("PATCH/films/id: start of partial updating of movie with id {}", id);
        Film updatedFilm = filmService.partialUpdate(id, updates);
        log.debug("PATCH/films/id: the process was completed successfully. A movie {} with id {} has been updated", updatedFilm.getName(), updatedFilm.getId());
        return updatedFilm;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFilm(@PathVariable Long id) {
        log.debug("DELETE/films/id: start of deleting of movie with id {}", id);
        Film film = filmService.deleteFilm(id);
        log.debug("DELETE/films/id: the process was completed successfully. A movie {} with id {} has been deleted", film.getName(), id);
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
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count){
        count = (count == null) ? 10 : count;
        log.debug("GET/films/popular: start of finding {} popular movie", count);
        Collection<Film> films = filmService.getPopularFilms(count);
        log.debug("GET/films/process: the process was completed successfully. The collection of {} popular movies has been returned", count);
        return films;
    }
}
