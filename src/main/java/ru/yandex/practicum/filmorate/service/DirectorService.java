package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;

import java.util.Collection;

public interface DirectorService {

    Collection<DirectorDto> findAll();

    DirectorDto findDirectorById(Long id);

    DirectorDto createDirector(NewDirectorRequest newDirectorRequest);

    DirectorDto updateDirector(UpdateDirectorRequest updateDirectorRequest);

    boolean deleteDirector(Long id);
}
