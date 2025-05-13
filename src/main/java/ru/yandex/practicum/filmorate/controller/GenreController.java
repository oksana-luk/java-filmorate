package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> findAll() {
        log.debug("GET /genres: the collection of genres of films has been returned");
        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    public GenreDto findGenreById(@PathVariable int id) {
        log.debug("GET/genres/id: start of finding of genre {}", id);
        GenreDto genreDto = genreService.findGenreById(id);
        log.debug("GET/films/id: the process was completed successfully. A genre {} with id {} has been found", genreDto.getName(), genreDto.getId());
        return genreDto;
    }
}
