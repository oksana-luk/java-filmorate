package ru.yandex.practicum.filmorate.controller;

//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.dto.film.FilmDto;
//import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
//import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
//import ru.yandex.practicum.filmorate.storage.user.UserStorage;
//
//import java.time.LocalDate;
//import java.util.Collection;
//
//import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

//    private FilmController filmController;
//    private final LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
//
//    @BeforeEach
//    void beforeEach() {
//        FilmStorage filmStorage = new InMemoryFilmStorage();
//        UserStorage userStorage = new InMemoryUserStorage();
//        FilmService filmService = new FilmService(filmStorage, userStorage,null, null, null, null);
//        filmController = new FilmController(filmService);
//    }
//
//    @Test
//    void shouldReturnAllFilms() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto filmDto = filmController.createFilm(newFilmRequest);
//        Collection<FilmDto> films = filmController.findAll();
//        assertNotNull(films, "Service didn't return all films.");
//        assertFalse(films.isEmpty(), "Service didn't return all films.");
//        assertTrue(films.contains(filmDto), "Service didn't return all films.");
//        assertEquals(1, films.size(), "Service didn't return all films.");
//    }
//
//    @Test
//    void shouldAddNewFilmAndSetId() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto filmDto = filmController.createFilm(newFilmRequest);
//
//        Collection<FilmDto> users = filmController.findAll();
//        assertTrue(users.contains(filmDto), "Film was not created or saved.");
//        assertEquals(1L, (long) filmDto.getId(), "Incorrect id was set.");
//    }
//
//    @Test
//    void shouldNotAddNewFilmWithReleaseDate28December1895() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate);
//        newFilmRequest.setDuration(90);
//        assertThrows(ValidationException.class, () -> filmController.createFilm(newFilmRequest), "New user with incorrect release date was created.");
//    }
//
//    @Test
//    void shouldNotAddNewFilmWithReleaseDateBefore28December1895() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.minusDays(1));
//        newFilmRequest.setDuration(90);
//        assertThrows(ValidationException.class, () -> filmController.createFilm(newFilmRequest), "New film with incorrect release date was created.");
//    }
//
//    @Test
//    void shouldNotAddNewFilmWithNegativeDuration() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(-90);
//        assertThrows(ValidationException.class, () -> filmController.createFilm(newFilmRequest), "New film with negative duration was created.");
//    }
//
//    @Test
//    void shouldNotUpdateFilmWithZeroId() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        filmController.createFilm(newFilmRequest);
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(0L);
//
//        assertThrows(NotFoundException.class, () -> filmController.updateFilm(updateFilmRequest));
//    }
//
//    @Test
//    void shouldNotUpdateFilmWithNotExistId() {
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(10L);
//
//        assertThrows(NotFoundException.class, () -> filmController.updateFilm(updateFilmRequest));
//    }
//
//    @Test
//    void shouldUpdateAll() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto createdFilm = filmController.createFilm(newFilmRequest);
//
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(createdFilm.getId());
//        updateFilmRequest.setName("newName");
//        updateFilmRequest.setDescription("newDescription");
//        updateFilmRequest.setReleaseDate(minReleaseDate.plusDays(2));
//        updateFilmRequest.setDuration(100);
//        FilmDto updatedFilm = filmController.updateFilm(updateFilmRequest);
//
//        assertNotNull(updatedFilm);
//        assertNotEquals(createdFilm.getName(), updatedFilm.getName());
//        assertNotEquals(createdFilm.getDescription(), updatedFilm.getDescription());
//        assertNotEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
//        assertNotEquals(createdFilm.getDuration(), updatedFilm.getDuration());
//    }
//
//    @Test
//    void shouldUpdateName() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto createdFilm = filmController.createFilm(newFilmRequest);
//
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(createdFilm.getId());
//        updateFilmRequest.setName("newName");
//        FilmDto updatedFilm = filmController.updateFilm(updateFilmRequest);
//
//        assertNotNull(updatedFilm);
//        assertEquals(updatedFilm.getName(), "newName");
//        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
//        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
//        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
//    }
//
//    @Test
//    void shouldUpdateDescription() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto createdFilm = filmController.createFilm(newFilmRequest);
//
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(createdFilm.getId());
//        updateFilmRequest.setDescription("newDescription");
//        FilmDto updatedFilm = filmController.updateFilm(updateFilmRequest);
//
//        assertNotNull(updatedFilm);
//        assertEquals(updatedFilm.getDescription(), "newDescription");
//        assertEquals(createdFilm.getName(), updatedFilm.getName());
//        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
//        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
//    }
//
//    @Test
//    void shouldUpdateReleaseDate() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(10);
//        FilmDto createdFilm = filmController.createFilm(newFilmRequest);
//
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(createdFilm.getId());
//        updateFilmRequest.setReleaseDate(minReleaseDate.plusDays(3));
//        FilmDto updatedFilm = filmController.updateFilm(updateFilmRequest);
//
//        assertNotNull(updatedFilm);
//        assertEquals(updatedFilm.getReleaseDate(), minReleaseDate.plusDays(3));
//        assertEquals(createdFilm.getName(), updatedFilm.getName());
//        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
//        assertEquals(createdFilm.getDuration(), updatedFilm.getDuration());
//    }
//
//    @Test
//    void shouldUpdateDuration() {
//        NewFilmRequest newFilmRequest = new NewFilmRequest();
//        newFilmRequest.setName("name");
//        newFilmRequest.setDescription("description");
//        newFilmRequest.setReleaseDate(minReleaseDate.plusDays(1));
//        newFilmRequest.setDuration(90);
//        FilmDto createdFilm = filmController.createFilm(newFilmRequest);
//
//        UpdateFilmRequest  updateFilmRequest = new UpdateFilmRequest();
//        updateFilmRequest.setId(createdFilm.getId());
//        updateFilmRequest.setDuration(120);
//        FilmDto updatedFilm = filmController.updateFilm(updateFilmRequest);
//
//        assertNotNull(updatedFilm);
//        assertEquals(updatedFilm.getDuration(), 120);
//        assertEquals(createdFilm.getName(), updatedFilm.getName());
//        assertEquals(createdFilm.getDescription(), updatedFilm.getDescription());
//        assertEquals(createdFilm.getReleaseDate(), updatedFilm.getReleaseDate());
//    }
}
