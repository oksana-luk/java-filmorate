package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        return Optional.ofNullable(films.get(id)).map(Film::new);
    }

    @Override
    public boolean deleteFilmById(Long id) {
        return films.remove(id) != null;
    }

    public Long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        if (likes.containsKey(id)) {
            likes.get(id).add(userId);
        } else {
            Set<Long> users = new HashSet<>();
            users.add(userId);
            likes.put(id, users);
        }
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        if (likes.containsKey(id)) {
            likes.get(id).remove(userId);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(Integer count) {
        Map<Long, Integer> likesCount = new HashMap<>();
        likes.keySet().forEach(id -> likesCount.put(id, likes.get(id).size()));

        Comparator<Integer> descendingComparator = (a, b) -> Integer.compare(b, a);

        return likesCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(descendingComparator))
                .limit(count)
                .map(Map.Entry::getKey)
                .map(films::get)
                .toList();
    }

    @Override
    public Collection<Film> getDirectorFilms(String sortBy, Long id) {
        return null;
    }
}




