package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    protected Long id;

    @EqualsAndHashCode.Include
    protected String email;

    @EqualsAndHashCode.Include
    protected String login;

    protected LocalDate birthday;

    protected String name;

    public User() {
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.birthday = user.getBirthday();
        this.name = user.getName();
    }
}
