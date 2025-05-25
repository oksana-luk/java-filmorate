package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public List<Event> getFeed(Long id) {
        return feedRepository.getFeed(id);
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
