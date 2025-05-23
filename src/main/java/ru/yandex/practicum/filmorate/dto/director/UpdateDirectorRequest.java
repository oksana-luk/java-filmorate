package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    @NotNull
    private Long id;

    private String name;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }
}
