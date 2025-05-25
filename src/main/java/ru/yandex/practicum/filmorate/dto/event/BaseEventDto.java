package ru.yandex.practicum.filmorate.dto.event;

import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

public interface BaseEventDto {
    Long getUserId();

    EventType getEventType();

    EventOperation getOperation();

    Long getEntityId();
}
