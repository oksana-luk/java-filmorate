package ru.yandex.practicum.filmorate.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

@Data
public class UpdateEventRequest {
    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    private EventType eventType;

    private EventOperation operation;

    private Long entityId;

    public boolean hasEventType() {
        return eventType != null;
    }

    public boolean hasOperation() {
        return operation != null;
    }

    public boolean hasEntityId() {
        return entityId != null;
    }
}
