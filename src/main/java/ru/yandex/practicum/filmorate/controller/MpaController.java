package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<MpaDto> findAll() {
        log.debug("GET /mpa: the collection of ratings of films has been returned");
        return mpaService.findAllMpa();
    }

    @GetMapping("/{id}")
    public MpaDto findMpaById(@PathVariable int id) {
        log.debug("GET/mpa/id: start of finding of rating {}", id);
        MpaDto mpaDto = mpaService.findMpaById(id);
        log.debug("GET/mpa/id: the process was completed successfully. A rating {} with id {} has been found", mpaDto.getName(), mpaDto.getId());
        return mpaDto;
    }
}
