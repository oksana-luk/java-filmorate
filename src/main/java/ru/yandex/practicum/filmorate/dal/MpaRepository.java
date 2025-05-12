package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MpaDto> {
    protected final MpaRowMapper mapper;
    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM ratings";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM ratings WHERE rating_id = ?";

    public MpaRepository(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    public Collection<MpaDto> getMpas() {
        return findMany(FIND_ALL_RATINGS_QUERY, mapper);
    }

    public Optional<MpaDto> findMpaById(int id) {
        return findOne(FIND_RATING_BY_ID, mapper, id);
    }
}
