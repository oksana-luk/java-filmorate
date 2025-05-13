package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public Collection<MpaDto> findAllMpa() {
        return mpaRepository.getMpas();
    }

    public MpaDto findMpaById(int id) {
        Optional<MpaDto> mpaOpt = mpaRepository.findMpaById(id);
        if (mpaOpt.isEmpty()) {
            String message = String.format("The service did not find rating by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        } else {
            return mpaOpt.get();
        }
    }

    private void setLogWarn(String message) {
        log.warn("The process ended with an error. {}", message);
    }
}
