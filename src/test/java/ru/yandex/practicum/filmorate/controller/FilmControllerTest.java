package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film film;
    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    void shouldReturnAllFilms() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        filmController.createFilm(film);
        Collection<Film> films = filmController.findAll();
        assertNotNull(films, "Service didn't return all films.");
        assertFalse(films.isEmpty(), "Service didn't return all films.");
        assertTrue(films.contains(film), "Service didn't return all films.");
        assertEquals(1, films.size(), "Service didn't return all films.");
    }

    @Test
    void shouldAddNewFilmAndSetId() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        Collection<Film> users = filmController.findAll();
        assertTrue(users.contains(film), "Film was not created or saved.");
        assertEquals(1L, (long) createdFilm.getId(), "Incorrect id was set.");
    }

    @Test
    void shouldNotAddNewFilmWithReleaseDate28December1895() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate);
        film.setDuration(Duration.ofMinutes(90));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film), "New user with incorrect release date was created.");
    }

    @Test
    void shouldNotAddNewFilmWithReleaseDateBefore28December1895() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(LocalDate.of(1895, 12, 28).minusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film), "New film with incorrect release date was created.");
    }

    @Test
    void shouldNotAddNewFilmWithNegativeDuration() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(-90));
        assertThrows(ValidationException.class, () -> filmController.createFilm(film), "New film with negative duration was created.");
    }

    @Test
    void shouldAddNewFilmWithZeroDuration() {
        film = new Film();
        film.setName("name");
        film.setDescription(" ");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(0));
        assertDoesNotThrow(() -> filmController.createFilm(film), "New film with zero duration was not created.");
        assertTrue(filmController.findAll().contains(film));
    }

    @Test
    void shouldNotUpdateFilmWithZeroId() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);
        createdFilm.setId(0L);

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void shouldNotUpdateFilmWithNotExistId() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);
        createdFilm.setId(10L);

        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
    }

    @Test
    void shouldUpdateAll() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        film = new Film();
        film.setId(createdFilm.getId());
        film.setName("newName");
        film.setDescription("newDescription");
        film.setReleaseDate(minReleaseDate.plusDays(2));
        film.setDuration(Duration.ofMinutes(100));
        Film updatedFilm = filmController.updateFilm(film);

        assertNotNull(updatedFilm);
        assertNotEquals(createdFilm.getName(), updatedFilm.getName());
        assertNotEquals(createdFilm.getDescription(), updatedFilm.getDescription());
        assertNotEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertNotEquals(createdFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    void shouldPartialUpdateName() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        film = new Film();
        film.setId(createdFilm.getId());
        film.setName("newName");
        Film updatedFilm = filmController.partialUpdate(film);

        assertNotNull(updatedFilm);
        assertEquals(updatedFilm.getName(), "newName");
        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    void shouldPartialUpdateDescription() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        film = new Film();
        film.setId(createdFilm.getId());
        film.setDescription("newDescription");
        Film updatedFilm = filmController.partialUpdate(film);

        assertNotNull(updatedFilm);
        assertEquals(updatedFilm.getDescription(), "newDescription");
        assertEquals(createdFilm.getName(), updatedFilm.getName());
        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    void shouldPartialUpdateReleaseDate() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        film = new Film();
        film.setId(createdFilm.getId());
        film.setReleaseDate(minReleaseDate.plusDays(3));
        Film updatedFilm = filmController.partialUpdate(film);

        assertNotNull(updatedFilm);
        assertEquals(updatedFilm.getReleaseDate(), minReleaseDate.plusDays(3));
        assertEquals(createdFilm.getName(), updatedFilm.getName());
        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
    }

    @Test
    void shouldPartialUpdateDuration() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);

        film = new Film();
        film.setId(createdFilm.getId());
        film.setDuration(Duration.ofMinutes(120));
        Film updatedFilm = filmController.partialUpdate(film);

        assertNotNull(updatedFilm);
        assertEquals(updatedFilm.getDuration(), Duration.ofMinutes(120));
        assertEquals(createdFilm.getName(), updatedFilm.getName());
        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
    }

    @Test
    void shouldNotPartialUpdateFilmWithZeroId() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);
        createdFilm.setId(0L);

        assertThrows(NotFoundException.class, () -> filmController.partialUpdate(film));
    }

    @Test
    void shouldNotPartialUpdateFilmWithNotExistId() {
        film = new Film();
        film.setName("name");
        film.setDescription("description");
        film.setReleaseDate(minReleaseDate.plusDays(1));
        film.setDuration(Duration.ofMinutes(90));
        Film createdFilm = filmController.createFilm(film);
        createdFilm.setId(10L);

        assertThrows(NotFoundException.class, () -> filmController.partialUpdate(film));
    }
}
