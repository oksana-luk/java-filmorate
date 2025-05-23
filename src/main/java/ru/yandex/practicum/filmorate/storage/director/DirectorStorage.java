package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> getDirectors();

    Optional<Director> findDirectorById(Long id);

    Director addDirector(Director director);

    Director updateDirector(Director director);

    boolean deleteDirector(Long id);

}
