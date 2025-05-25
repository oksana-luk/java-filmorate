package ru.yandex.practicum.filmorate.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

@Data
public class NewEventRequest {
    private Long timestamp;

    @NotNull(message = "User  ID cannot be null")
    private Long userId;

    @NotNull(message = "Event Type cannot be null")
    private EventType eventType;

    @NotNull(message = "Operation cannot be null")
    private EventOperation operation;

    @NotNull(message = "Entity ID cannot be null")
    private Long entityId;
}
