package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.event.NewEventRequest;
import ru.yandex.practicum.filmorate.dto.event.UpdateEventRequest;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event mapToEvent(NewEventRequest request) {
        Event event = new Event();
        event.setTimestamp(request.getTimestamp());
        event.setUserId(request.getUserId());
        event.setEventType(request.getEventType());
        event.setOperation(request.getOperation());
        event.setEntityId(request.getEntityId());
        return event;
    }

    public static EventDto mapToEventDto(Event event) {
        EventDto dto = new EventDto();
        dto.setTimestamp(event.getTimestamp());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType());
        dto.setOperation(event.getOperation());
        dto.setEventId(event.getEventId());
        dto.setEntityId(event.getEntityId());
        return dto;
    }

    public static Event updateEventFields(Event event, UpdateEventRequest request) {
        if (request.hasEventType()) {
            event.setEventType(request.getEventType());
        }
        if (request.hasOperation()) {
            event.setOperation(request.getOperation());
        }
        if (request.hasEntityId()) {
            event.setEntityId(request.getEntityId());
        }
        return event;
    }
}
