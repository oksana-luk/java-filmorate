package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(ResultSet rs) throws DataAccessException {
        try {
            if (!rs.next()) {
                return null;
            }
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            Timestamp registrationDate = rs.getTimestamp("release_date");
            if (registrationDate != null) {
                film.setReleaseDate(registrationDate.toLocalDateTime().toLocalDate());
            }
            film.setMpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating_name")));
            do {
                if (rs.getInt("genre_id") != 0) {
                    film.addGenre(rs.getInt("genre_id"), rs.getString("genre_name"));
                }
            } while (rs.next());
            return film;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при извлечении данных о фильме", e) {
            };
        }
    }
}
