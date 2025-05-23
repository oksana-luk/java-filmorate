package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mappers.FilmListResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Component("filmRepository")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    protected final FilmResultSetExtractor resultSetExtractor;
    protected final FilmListResultSetExtractor listResultSetExtractor;

    private static final String FIND_ALL_FILMS_QUERY = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id;""";
    private static final String FIND_FILM_BY_ID_QUERY = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                        WHERE films.film_id = ?;""";
    private static final String INSERT_FILM_QUERY = """
                                                    INSERT INTO films (name, description, release_date, duration, rating_id)
                                                    VALUES (?,?,?,?,?)""";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = """
                                                    UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
                                                    WHERE film_id = ?""";
    private static final String DELETE_FILM_QUERY = "DELETE films WHERE film_id = ?";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE film_genres WHERE film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = """
                                                            SELECT films.*,
                                                            fg.genre_id,
                                                            g.name AS genre_name,
                                                            r.name AS rating_name
                                                            FROM (	SELECT likes.film_id,
                                                                            COUNT(likes.like_id) AS count_likes
                                                                    FROM likes
                                                                    GROUP BY film_id
                                                                    ORDER BY COUNT(likes.like_id) DESC
                                                                    LIMIT ?) AS c
                                                            LEFT JOIN films AS films ON c.film_id = films.film_id
                                                            LEFT JOIN film_genres AS fg ON c.film_id = fg.film_id
                                                            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                            LEFT JOIN ratings AS r ON films.rating_id = r.rating_id;""";
    private static final String FIND_COMMON_FILMS_QUERY = """
                                                            SELECT films.*,
                                                                fg.genre_id,
                                                                g.name AS genre_name,
                                                                r.name AS rating_name
                                                            FROM films
                                                                LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                                LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                                LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                            WHERE films.film_id IN (
                                                                SELECT film_id
                                                                FROM likes l
                                                                WHERE user_id IN (?, ?)
                                                                GROUP BY film_id
                                                                HAVING COUNT(DISTINCT user_id) = 2
                                                                ORDER BY (
                                                                    SELECT COUNT(*)
                                                                    FROM likes l2
                                                                    WHERE l2.film_id = l.film_id
                                                                    ) DESC
                                                                )""";

    @Autowired
    public FilmRepository(JdbcTemplate jdbc, FilmResultSetExtractor resultSetExtractor,
                          FilmListResultSetExtractor listResultSetExtractor) {
        super(jdbc);
        this.resultSetExtractor = resultSetExtractor;
        this.listResultSetExtractor = listResultSetExtractor;
    }

    @Override
    public Collection<Film> getFilms() {
        return extractMany(FIND_ALL_FILMS_QUERY, listResultSetExtractor);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return extractOne(FIND_FILM_BY_ID_QUERY, resultSetExtractor, id);
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        for (Genre genre : film.getGenres()) {
            try {
                insert(INSERT_FILM_GENRE_QUERY, id, genre.getId());
            } catch (DuplicateKeyException ignored) {
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        delete(DELETE_FILM_GENRE_QUERY, film.getId());
        for (Genre genre : film.getGenres()) {
            try {
                insert(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
            } catch (DuplicateKeyException ignored) {
            }
        }
        return film;
    }

    @Override
    public boolean deleteFilmById(Long id) {
        return delete(DELETE_FILM_QUERY, id) && delete(DELETE_FILM_GENRE_QUERY, id);
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        insert(INSERT_LIKE_QUERY, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        delete(DELETE_LIKE_QUERY, id, userId);
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        return extractMany(FIND_POPULAR_FILMS_QUERY, listResultSetExtractor, count);
    }

    @Override
    public Collection<Film> getCommonFilmsFriends(Long userId, Long friendId) {
        return extractMany(FIND_COMMON_FILMS_QUERY, listResultSetExtractor, userId, friendId);
    }
}