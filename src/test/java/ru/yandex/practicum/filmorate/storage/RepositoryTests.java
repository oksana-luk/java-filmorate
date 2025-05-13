package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru.yandex.practicum.filmorate.dal.mappers")
@Import({UserRepository.class, FilmRepository.class, GenreRepository.class, MpaRepository.class})
public class RepositoryTests {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Test
    public void testFindUserById() {
        Optional<User> userOpt = userRepository.findUserById(1L);
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testFindFilmById() {
        Optional<Film> filmOpt = filmRepository.findFilmById(1L);
        assertThat(filmOpt)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testFindGenreById() {
        Optional<GenreDto> genreOpt = genreRepository.findGenreById(1);
        assertThat(genreOpt)
                .isPresent()
                .hasValueSatisfying(genre -> assertThat(genre).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void testFindRatingById() {
        Optional<MpaDto> mpaById = mpaRepository.findMpaById(1);
        assertThat(mpaById)
                .isPresent()
                .hasValueSatisfying(mpa -> assertThat(mpa).hasFieldOrPropertyWithValue("id", 1));
    }
}
