package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        Event event = new Event();
        //event.setTimestamp(rs.getTimestamp("timestamp").getTime());
        event.setTimestamp(rs.getLong("timestamp"));
        event.setUserId(rs.getLong("user_Id"));
        event.setEventType(EventType.valueOf(rs.getString("eventType")));
        event.setOperation(EventOperation.valueOf(rs.getString("operation")));
        event.setEventId(rs.getLong("event_Id"));
        event.setEntityId(rs.getLong("entity_Id"));
        return event;
    }
}
