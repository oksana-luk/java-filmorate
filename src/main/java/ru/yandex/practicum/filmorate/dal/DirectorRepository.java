package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {
    protected final DirectorRowMapper mapper;

    private static final String FIND_ALL = "SELECT * FROM directors";
    private static final String FIND_ONE = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_DIRECTOR = """
            INSERT INTO directors (name)
            VALUES (?)
            """;
    private static final String UPDATE_DIRECTOR = """
            UPDATE directors
            SET name = ?
            WHERE id = ?
            """;

    private static final String DELETE_DIRECTOR = "DELETE directors WHERE id = ?";

    public DirectorRepository(JdbcTemplate jdbc, DirectorRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public Collection<Director> getDirectors() {
        return findMany(FIND_ALL, mapper);
    }

    @Override
    public Optional<Director> findDirectorById(Long id) {
        return findOne(FIND_ONE, mapper, id);
    }

    @Override
    public Director addDirector(Director director) {
        long id = insert(
                INSERT_DIRECTOR,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        update(
                UPDATE_DIRECTOR,
                director.getName(),
                director.getId()
        );
        return director;
    }

    @Override
    public boolean deleteDirector(Long id) {
        return delete(DELETE_DIRECTOR, id);
    }

}
