package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Repository
public class FeedRepository extends BaseRepository<Event> {
    protected final EventRowMapper mapper;

    private static final String GET_FEED = "SELECT * FROM eventy WHERE user_Id = ?";
    private static final String ADD_FEED = """
                                           INSERT INTO eventy (timestamp, user_Id, eventType, operation, entity_Id)
                                           VALUES (?, ?, ?, ?, ?)
                                           """;

    public FeedRepository(JdbcTemplate jdbc, EventRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    public List<Event> getFeed(Long id) {
        return jdbc.query(GET_FEED, mapper, id);
    }

    public void addEvent(Event event) {
        long id = insert(ADD_FEED,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId());
        event.setEventId(id);
        log.info("добавили событие в базу");
    }
}
