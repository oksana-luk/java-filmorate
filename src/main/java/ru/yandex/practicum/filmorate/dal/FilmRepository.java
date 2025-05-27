package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.mappers.FilmListResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Repository
@Component("filmRepository")
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    protected final FilmResultSetExtractor resultSetExtractor;
    protected final FilmListResultSetExtractor listResultSetExtractor;

    private static final String FIND_ALL_FILMS = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        df.director_id,
                                                        d.name AS director_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN likes AS l ON films.film_id = l.film_id
                                                        LEFT JOIN director_film df ON films.film_id = df.film_id
                                                        LEFT JOIN directors AS d ON df.director_id = d.id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id""";

    private static final String FIND_ALL_FILMS_QUERY = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        df.director_id,
                                                        d.name AS director_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN director_film df ON films.film_id = df.film_id
                                                        LEFT JOIN directors AS d ON df.director_id = d.id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id;""";
    private static final String FIND_FILM_BY_ID_QUERY = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        df.director_id,
                                                        d.name AS director_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN director_film df ON films.film_id = df.film_id
                                                        LEFT JOIN directors AS d ON df.director_id = d.id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                        WHERE films.film_id = ?;""";
    private static final String INSERT_FILM_QUERY = """
                                                    INSERT INTO films (name, description, release_date, duration, rating_id)
                                                    VALUES (?,?,?,?,?)""";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_DIRECTOR_FILM_QUERY = "INSERT INTO director_film (director_id, film_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = """
                                                    UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
                                                    WHERE film_id = ?""";
    private static final String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String DELETE_FILM_GENRE_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String DELETE_DIRECTOR_FILM_QUERY = "DELETE FROM director_film WHERE film_id = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = """
                                                            SELECT films.*,
                                                            COALESCE(c.countLike, 0) AS countLike,
                                                            fg.genre_id,
                                                            g.name AS genre_name,
                                                            df.director_id,
                                                            d.name AS director_name,
                                                            r.name AS rating_name
                                                            FROM films
                                                            LEFT JOIN (SELECT lk.film_id,
                                                                        COUNT(*) as countLike
                                                                        FROM likes AS lk
                                                                        GROUP BY lk.film_id) AS c ON films.film_id = c.film_id
                                                            LEFT JOIN film_genres AS fg ON films.film_id = fg.film_id
                                                            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                            LEFT JOIN director_film df ON films.film_id = df.film_id
                                                            LEFT JOIN directors AS d ON df.director_id = d.id
                                                            LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                            #join#
                                                            ORDER BY COALESCE(c.countLike, 0) DESC
                                                            LIMIT ?;""";

    private static final String FIND_COMMON_FILMS_QUERY = """
                                                            SELECT films.*,
                                                                fg.genre_id,
                                                                g.name AS genre_name,
                                                                df.director_id,
                                                                d.name AS director_name,
                                                                r.name AS rating_name
                                                            FROM films
                                                                LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                                LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                                LEFT JOIN director_film df ON films.film_id = df.film_id
                                                                LEFT JOIN directors AS d ON df.director_id = d.id
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

    private static final String FIND_RECOMMENDATIONS_QUERY = """
                                                            SELECT films.*,
                                                            fg.genre_id,
                                                            g.name AS genre_name,
                                                            r.name AS rating_name,
                                                             df.director_id,
                                                            d.name AS director_name
                                                            FROM likes AS likes
                                                            LEFT JOIN films AS films ON likes.film_id = films.film_id
                                                            LEFT JOIN film_genres AS fg ON films.film_id = fg.film_id
                                                            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                            LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                            LEFT JOIN director_film df ON films.film_id = df.film_id
                                                            LEFT JOIN directors AS d ON df.director_id = d.id
                                                            WHERE likes.user_id = ?
                                                            AND NOT likes.film_id IN (SELECT l.FILM_ID
                                                                                    FROM LIKES l
                                                                                    WHERE l.USER_ID = ?)""";


    private static final String FIND_DIRECTOR_FILMS_BY_LIKES = """
                                                            SELECT films.*,
                                                            fg.genre_id,
                                                            g.name AS genre_name,
                                                            df.director_id,
                                                            d.name AS director_name,
                                                            r.name AS rating_name,
                                                            COALESCE(c.count_likes, 0) AS likes_quantity
                                                            FROM films AS films
                                                            LEFT JOIN film_genres AS fg ON films.film_id = fg.film_id
                                                            LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                            LEFT JOIN director_film df ON films.film_id = df.film_id
                                                            LEFT JOIN directors AS d ON df.director_id = d.id
                                                            LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                            LEFT JOIN (	SELECT likes.film_id,
                                                                            COUNT(likes.like_id) AS count_likes
                                                                    FROM likes
                                                                    GROUP BY film_id
                                                                    ) AS c ON films.film_id = c.film_id
                                                            WHERE df.director_id = ?
                                                            ORDER BY likes_quantity DESC;""";

    private static final String FIND_DIRECTOR_FILMS_BY_YEAR = """
                                                        SELECT films.*,
                                                        fg.genre_id,
                                                        g.name AS genre_name,
                                                        df.director_id,
                                                        d.name AS director_name,
                                                        r.name AS rating_name
                                                        FROM films
                                                        LEFT JOIN film_genres fg ON films.film_id = fg.film_id
                                                        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                                                        LEFT JOIN director_film df ON films.film_id = df.film_id
                                                        LEFT JOIN directors AS d ON df.director_id = d.id
                                                        LEFT JOIN ratings AS r ON films.rating_id = r.rating_id
                                                        WHERE df.director_id = ?
                                                        ORDER BY YEAR(films.release_date) ASC;""";
    private static final String ORDER_BY_LIKES = """
                                                    ORDER BY (
                                                          SELECT COUNT(*)
                                                          FROM likes l2
                                                          WHERE l2.film_id = l.film_id
                                                          ) DESC""";

    private static final String FIND_FILMS_BY_TITLE = " WHERE films.name ILike ? ";
    private static final String FIND_FILMS_BY_DIRECTOR = " WHERE d.name ILike ? ";
    private static final String FIND_FILMS_BY_DIRECTOR_AND_TITLE = " WHERE d.name ILike ? OR films.name ILike ? ";

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
        for (Director director: film.getDirectors()) {
            try {
                jdbc.update(INSERT_DIRECTOR_FILM_QUERY, director.getId(), film.getId());
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
        jdbc.update(DELETE_FILM_GENRE_QUERY, film.getId());
        jdbc.update(DELETE_DIRECTOR_FILM_QUERY, film.getId());
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                try {
                    jdbc.update(INSERT_FILM_GENRE_QUERY, film.getId(), genre.getId());
                } catch (DuplicateKeyException ignored) {
                }
            }
        }
        for (Director director: film.getDirectors()) {
            try {
                jdbc.update(INSERT_DIRECTOR_FILM_QUERY, director.getId(), film.getId());
            } catch (DuplicateKeyException ignored) {
            }
        }
        return film;
    }

    @Override
    public boolean deleteFilmById(Long id) {
        return delete(DELETE_FILM_QUERY, id);
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
    public Collection<Film> getPopularFilms(Integer count, Integer genreId, LocalDate yearDate) {
        String baseQuery = FIND_POPULAR_FILMS_QUERY;
        String appendJoin = "";
        if (!Objects.isNull(genreId) && !Objects.isNull(yearDate)) {
            appendJoin = """
                WHERE DATEDIFF(year, films.release_date, ?)=0 AND films.film_id NOT IN (SELECT fmg.film_id
                                                                                        FROM film_genres AS fmg
                                                                                        WHERE genre_id = ?)""";
            baseQuery = baseQuery.replace("#join#", appendJoin);
            return extractMany(baseQuery, listResultSetExtractor, yearDate, genreId, count);
        } else if (!Objects.isNull(genreId)) {
            appendJoin = """
                WHERE films.film_id NOT IN (SELECT fmg.film_id
                                             FROM film_genres AS fmg
                                             WHERE genre_id = ?)""";
            baseQuery = baseQuery.replace("#join#", appendJoin);
            return extractMany(baseQuery, listResultSetExtractor, genreId, count);
        } else if (!Objects.isNull(yearDate)) { //
            appendJoin = """
                WHERE DATEDIFF(year, films.release_date, ?)=0""";
            baseQuery = baseQuery.replace("#join#", appendJoin);
            return extractMany(baseQuery, listResultSetExtractor, yearDate, count);
        } else { //
            baseQuery = baseQuery.replace("#join#", "");
            return extractMany(baseQuery, listResultSetExtractor, count);
        }
    }

    @Override
    public Collection<Film> getRecommendations(Long userId, Long otherUserId) {
        return extractMany(FIND_RECOMMENDATIONS_QUERY, listResultSetExtractor, otherUserId, userId);
    }

    @Override
    public Collection<Film> getDirectorFilms(String sortBy, Long directorId) {
        if (Objects.equals(sortBy, "likes")) {
            return extractMany(FIND_DIRECTOR_FILMS_BY_LIKES, listResultSetExtractor, directorId);
        } else if (Objects.equals(sortBy, "year")) {
            return extractMany(FIND_DIRECTOR_FILMS_BY_YEAR, listResultSetExtractor, directorId);
        }
        throw new NotFoundException("Wrong parameter for sorting");
    }

    @Override
    public Collection<Film> getFriendsCommonFilms(Long userId, Long friendId) {
        return extractMany(FIND_COMMON_FILMS_QUERY, listResultSetExtractor, userId, friendId);
    }

    @Override
    public Collection<Film> searchByTitleAndDirector(String query) {
        return extractMany(FIND_ALL_FILMS
                + FIND_FILMS_BY_DIRECTOR_AND_TITLE
                + ORDER_BY_LIKES, listResultSetExtractor, "%" + query + "%", "%" + query + "%");
    }

    @Override
    public Collection<Film> searchByTitle(String query) {
        return extractMany(FIND_ALL_FILMS
                + FIND_FILMS_BY_TITLE
                + ORDER_BY_LIKES, listResultSetExtractor, "%" + query + "%");
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        return extractMany(FIND_ALL_FILMS
                + FIND_FILMS_BY_DIRECTOR
                + ORDER_BY_LIKES, listResultSetExtractor, "%" + query + "%");
    }
}
