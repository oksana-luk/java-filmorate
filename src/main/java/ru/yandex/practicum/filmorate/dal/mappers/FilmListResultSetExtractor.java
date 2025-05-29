package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Component
public class FilmListResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> idToFilm = new LinkedHashMap<>();
        while (rs.next()) {
            Long currentId = rs.getLong("film_id");
            Film film;
            if (idToFilm.containsKey(currentId)) {
                film = idToFilm.get(currentId);
            } else {
                film = new Film();
                film.setId(currentId);
                film.setName(rs.getString("name"));
                film.setDescription((rs.getString("description")));
                film.setDuration(rs.getInt("duration"));
                Timestamp registrationDate = rs.getTimestamp("release_date");
                film.setReleaseDate(registrationDate.toLocalDateTime().toLocalDate());
                idToFilm.put(currentId, film);
                film.setMpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")));
            }
            int genreId = rs.getInt("genre_id");
            if (rs.getInt("genre_id") != 0
                    && film.getGenres().stream().noneMatch(g -> g.getId() == genreId)) {
                film.addGenre(genreId, rs.getString("genre_name"));
            }
            long directorId = rs.getLong("director_id");
            if (rs.getLong("director_id") != 0
                    && film.getDirectors().stream().noneMatch(d -> d.getId() == directorId)) {
                film.addDirector(directorId, rs.getString("director_name"));
            }
        }
        return idToFilm.values().stream().toList();
    }
}

