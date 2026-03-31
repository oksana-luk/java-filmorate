package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback()
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.data-locations=")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
    private final UserRowMapper userRowMapper;

    @Test
    void saveUser() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("oksana");
        newUserRequest.setEmail("oksana@yande.ru");
        newUserRequest.setLogin("chuff");
        newUserRequest.setBirthday(LocalDate.of(2000, 12, 12));

        userService.createUser(newUserRequest);

        String sql = "SELECT * FROM users WHERE email = ?";

        User user = jdbcTemplate.queryForObject(sql, userRowMapper, newUserRequest.getEmail());

        assertThat(user, notNullValue());
        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(newUserRequest.getEmail()));
        assertThat(user.getName(), equalTo(newUserRequest.getName()));
        assertThat(user.getLogin(), equalTo(newUserRequest.getLogin()));
        assertThat(user.getBirthday(), equalTo(newUserRequest.getBirthday()));
    }
}