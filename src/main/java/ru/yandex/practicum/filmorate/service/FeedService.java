package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    public FeedService(FeedRepository feedRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.feedRepository = feedRepository;
        this.userRepository = userRepository;
    }

    public List<EventDto> getFeed(Long id) {
        Optional<User> userOpt = userRepository.findUserById(id);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        List<Event> events = feedRepository.getFeed(id);
        return events.stream()
                .map(EventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

    public void addEvent(Long userId, EventType eventType, EventOperation operation, Long entityId) {
        if (userId == null || eventType == null || operation == null || entityId == null) {
            throw new NotFoundException("Параметры не могут быть null");
        }
        Event event = new Event(
                Instant.now().toEpochMilli(),
                userId,
                eventType,
                operation,
                null,
                entityId
        );
        feedRepository.addEvent(event);
        log.info("Получена лента событий пользователя с id: {}", userId);
    }
}
