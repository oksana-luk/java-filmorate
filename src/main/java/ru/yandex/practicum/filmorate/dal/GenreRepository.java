package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.Collection;
import java.util.Optional;


@Repository
public class GenreRepository extends BaseRepository<GenreDto> {
    protected final GenreRowMapper mapper;

    private static final String FIND_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = ?";

    public GenreRepository(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    public Collection<GenreDto> getGenres() {
        return findMany(FIND_GENRES_QUERY, mapper);
    }

    public Optional<GenreDto> findGenreById(int id) {
        return findOne(FIND_GENRE_BY_ID_QUERY, mapper, id);
    }
}
