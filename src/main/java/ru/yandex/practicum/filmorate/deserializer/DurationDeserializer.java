package ru.yandex.practicum.filmorate.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String durationString = parser.getText();
        int durationSeconds = Integer.parseInt(durationString);
        return Duration.ofSeconds(durationSeconds);
    }
}

