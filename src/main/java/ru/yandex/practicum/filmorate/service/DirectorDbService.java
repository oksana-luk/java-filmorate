package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectorDbService implements DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorDbService(@Qualifier("directorRepository") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<DirectorDto> findAll() {
        return directorStorage.getDirectors()
                .stream()
                .map(DirectorMapper::mapToDirectorDto)
                .collect(Collectors.toList());
    }

    @Override
    public DirectorDto findDirectorById(Long id) {
        return DirectorMapper.mapToDirectorDto(directorStorage.findDirectorById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Жанр " + id + " не найден");
            log.error(e.getMessage());
            return e;
        }));
    }

    @Override
    public DirectorDto createDirector(NewDirectorRequest newDirectorRequest) {
        Director director = DirectorMapper.mapToDirector(newDirectorRequest);
        return DirectorMapper.mapToDirectorDto(directorStorage.addDirector(director));
    }

    @Override
    public DirectorDto updateDirector(UpdateDirectorRequest updateDirectorRequest) {
        Director director = validateNotFound(updateDirectorRequest.getId());
        director = DirectorMapper.updateDirectorFields(director, updateDirectorRequest);
        return DirectorMapper.mapToDirectorDto(directorStorage.updateDirector(director));
    }

    @Override
    public boolean deleteDirector(Long id) {
        validateNotFound(id);
        return directorStorage.deleteDirector(id);
    }

    private Director validateNotFound(Long id) {
        return directorStorage.findDirectorById(id).orElseThrow(() -> {
            NotFoundException e = new NotFoundException("Директор " + id + " не найден");
            log.error(e.getMessage());
            return e;
        });
    }

}
