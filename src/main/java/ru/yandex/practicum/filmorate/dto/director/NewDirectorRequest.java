package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewDirectorRequest implements BaseDirectorDto {

    @NotNull(message = "Director name should not be empty")
    @NotEmpty(message = "Director name should not be empty")
    @NotBlank(message = "Director name should not be empty")
    private String name;
}
