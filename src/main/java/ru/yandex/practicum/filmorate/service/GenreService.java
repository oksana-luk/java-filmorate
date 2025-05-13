package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Collection<GenreDto> findAllGenres() {
        return genreRepository.getGenres();
    }

    public GenreDto findGenreById(int id) {
        Optional<GenreDto> genreOpt = genreRepository.findGenreById(id);
        if (genreOpt.isEmpty()) {
            String message = String.format("The service did not find a genre by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        } else {
            return genreOpt.get();
        }
    }

    private void setLogWarn(String message) {
        log.warn("The process ended with an error. {}", message);
    }
}
