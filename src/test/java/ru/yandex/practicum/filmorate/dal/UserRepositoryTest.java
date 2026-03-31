package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    public void testFindUserById() {
        Optional<User> userOpt = userRepository.findUserById(1L);
        assertThat(userOpt)
                .isPresent()
                .hasValueSatisfying(user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void shouldInsertUserAndReturnId() {
        User testUser = getTestUser();

        User user = userRepository.addUser(testUser);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(testUser.getLogin(), user.getLogin());
        Assertions.assertEquals(testUser.getEmail(), user.getEmail());
        Assertions.assertEquals(testUser.getBirthday(), user.getBirthday());
        Assertions.assertEquals(testUser.getName(), user.getName());
    }

    @Test
    public void shouldThrowWhenEmailIsNull() {
        User user = getTestUser();
        user.setEmail(null);
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.addUser(user)
        );
    }

    @Test
    public void shouldThrowWhenEmailIsNotUnique() {
        User testUser = getTestUser();
        userRepository.addUser(testUser);
        testUser.setId(null);
        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.addUser(testUser)
        );
    }

    @Test
    public void shouldThrowWhenChangeId() {
        User testUser = getTestUser();
        User user = userRepository.addUser(testUser);
        user.setId(testUser.getId() + 1);
        Assertions.assertThrows(InternalServerException.class, () ->
                userRepository.updateUser(user)
        );
    }

    @Test
    public void shouldChangeNameLoginBirthday() {
        String newName = "newName";
        String newLogin = "newLogin";
        LocalDate newBirthday = LocalDate.of(2002, 2, 2);
        String newEmail = "new@new.com";
        User testUser = getTestUser();
        User user = userRepository.addUser(testUser);
        user.setName(newName);
        user.setLogin(newLogin);
        user.setBirthday(newBirthday);
        user.setEmail(newEmail);
        User updatedUser = userRepository.updateUser(user);

        assertEquals(updatedUser.getName(), newName);
        assertEquals(updatedUser.getLogin(), newLogin);
        assertEquals(updatedUser.getEmail(), newEmail);
        assertEquals(updatedUser.getBirthday(), newBirthday);
    }

    private static User getTestUser() {
        User testUser = new User();
        testUser.setName("name");
        testUser.setEmail("test@test.com");
        testUser.setLogin("login");
        testUser.setBirthday(LocalDate.of(2000, 1, 1));
        return testUser;
    }
}