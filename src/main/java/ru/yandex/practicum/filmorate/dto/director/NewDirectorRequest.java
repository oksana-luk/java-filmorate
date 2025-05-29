package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewDirectorRequest implements BaseDirectorDto {


    @NotBlank(message = "Director name should not be empty")
    private String name;
}
