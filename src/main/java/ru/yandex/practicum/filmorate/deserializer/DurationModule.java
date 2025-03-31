package ru.yandex.practicum.filmorate.deserializer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.jackson.JsonComponent;

import java.time.Duration;

@JsonComponent
public class DurationModule extends SimpleModule {
    public DurationModule() {
        addSerializer(Duration.class, new DurationSerializer());
        addDeserializer(Duration.class, new DurationDeserializer());
    }
}

