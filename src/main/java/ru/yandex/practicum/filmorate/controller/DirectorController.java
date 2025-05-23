package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/directors")
@Validated
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(@Qualifier("directorDbService") DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public Collection<DirectorDto> findAll() {
        log.debug("GET /directors: the collection of directors has been returned");
        return directorService.findAll();
    }

    @GetMapping("/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public DirectorDto findDirectorById(@Valid @PathVariable long directorId) {
        log.debug("GET/directors/id: start of finding of director {}", directorId);
        DirectorDto directorDto = directorService.findDirectorById(directorId);
        log.debug("GET/directors/id: the process was completed successfully. A new director {} with id {} has been found", directorDto.getName(), directorDto.getId());
        return directorDto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto createDirector(@Valid @RequestBody NewDirectorRequest newDirectorRequest) {
        log.debug("POST/directors: start of creating of new director {}", newDirectorRequest.getName());
        DirectorDto directorDto = directorService.createDirector(newDirectorRequest);
        log.debug("POST/directors: the process was completed successfully. A new director {} with id {} has been created", directorDto.getName(), directorDto.getId());
        return directorDto;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public DirectorDto updateDirector(@Valid @RequestBody UpdateDirectorRequest updateDirectorRequest) {
        log.debug("PUT/directors: start of updating of director {}", updateDirectorRequest.getName());
        DirectorDto directorDto = directorService.updateDirector(updateDirectorRequest);
        log.debug("PUT/directors: the process was completed successfully. A director {} with id {} has been updated", updateDirectorRequest.getName(), updateDirectorRequest.getId());
        return directorDto;
    }

    @DeleteMapping("/{directorId}")
    public ResponseEntity<Map<String, String>> deleteDirector(@PathVariable Long directorId) {
        log.debug("DELETE/director/id: start of deleting of director with id {}", directorId);
        directorService.deleteDirector(directorId);
        log.debug("DELETE/director/id: the process was completed successfully. A director with id {} has been deleted", directorId);
        return ResponseEntity.ok(Map.of("result",String.format("Director with id %s has been deleted successfully.", directorId)));
    }
}
