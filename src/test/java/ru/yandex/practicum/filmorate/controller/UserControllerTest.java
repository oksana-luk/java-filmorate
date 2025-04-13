package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldReturnAllUsers() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setEmail("emai@mail.com");
        userController.createUser(user);
        Collection<User> users = userController.findAll();
        assertNotNull(users, "Service didn't return all users.");
        assertFalse(users.isEmpty(), "Service didn't return all users.");
        assertTrue(users.contains(user), "Service didn't return all users.");
        assertEquals(1, users.size(), "Service didn't return all users.");
    }

    @Test
    void shouldAddNewUserAndSetId() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Collection<User> users = userController.findAll();
        assertTrue(users.contains(user), "The user was not created or saved.");
        assertEquals(1L, (long) createdUser.getId(), "Incorrect id was set by user.");
    }

    @Test
    void shouldNotAddNewUserWithDuplicateEmail() {
        String duplicateEmail = "first@email.com";
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setEmail(duplicateEmail);
        userController.createUser(user);

        User secondUser = new User();
        secondUser.setName("name2");
        secondUser.setLogin("login2");
        secondUser.setBirthday(LocalDate.of(2002, 2, 2));
        secondUser.setEmail(duplicateEmail);
        assertThrows(DuplicateException.class, () -> userController.createUser(secondUser), "New user with incorrect email was created.");
    }

    @Test
    void shouldNotAddNewUserLoginContainsSpace() {
        user = new User();
        user.setName("name");
        user.setLogin("my login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setEmail("my@email.com");
        assertThrows(ValidationException.class, () -> userController.createUser(user), "New user with incorrect login with spase was created.");
    }

    @Test
    void shouldAddNewUserWithoutNameAndSetNameFromLogin() {
        user = new User();
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldAddNewUserWithEmptyNameAndSetNameFromLogin() {
        user = new User();
        user.setName("");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldAddNewUserWithBlankNameAndSetNameFromLogin() {
        user = new User();
        user.setName(" ");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldNotUpdateUserWithZeroId() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);
        createdUser.setId(0L);

        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    void shouldNotUpdateUserWithNotExistId() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);
        createdUser.setId(10L);

        assertThrows(NotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    void shouldNotUpdateUserWithDuplicateEmail() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.createUser(user);

        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        user = new User();
        user.setId(createdUser.getId());
        user.setLogin("secondLogin");
        user.setName("secondName");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.setEmail("my1email@mail.com");
        assertThrows(DuplicateException.class, () -> userController.updateUser(user));
    }

    @Test
    void shouldUpdateAllFields() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        user = new User();
        user.setId(createdUser.getId());
        user.setName("newName");
        user.setLogin("newLogin");
        user.setEmail("newEmail@mail.com");
        user.setBirthday(LocalDate.of(1999, 1, 1));
        User updatedUser = userController.updateUser(user);

        assertNotNull(updatedUser);
        assertNotEquals(createdUser.getName(), updatedUser.getName());
        assertNotEquals(createdUser.getLogin(), updatedUser.getLogin());
        assertNotEquals(createdUser.getEmail(), updatedUser.getEmail());
        assertNotEquals(createdUser.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    void shouldPartialUpdate_Login() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Map<String, Object> updates = Map.of("login", "newLogin");
        User updatedUser = userController.partialUpdate(createdUser.getId(), updates);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), "newLogin");
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void shouldPartialUpdate_Email() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Map<String, Object> updates = Map.of("email", "my3mail@mail.com");
        User updatedUser = userController.partialUpdate(createdUser.getId(), updates);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), "my3mail@mail.com");
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void shouldPartialUpdate_Birthday() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Map<String, Object> updates = Map.of("birthday", "2012-12-12");
        User updatedUser = userController.partialUpdate(createdUser.getId(), updates);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), LocalDate.of(2012, 12, 12));
    }

    @Test
    void shouldPartialUpdate_Name() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Map<String, Object> updates = Map.of("name", "newName");
        User updatedUser = userController.partialUpdate(createdUser.getId(), updates);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), "newName");
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void shouldNotPartialUpdateUserWithDuplicateEmail() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.createUser(user);

        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my2email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        User createdUser = userController.createUser(user);

        Map<String, Object> updates = Map.of("email", "my1email@mail.com");

        assertThrows(DuplicateException.class, () -> userController.partialUpdate(createdUser.getId(), updates));
    }

    @Test
    void shouldNotPartialUpdateUserWithZeroId() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.createUser(user);

        Map<String, Object> updates = Map.of("name", "secondName");

        assertThrows(NotFoundException.class, () -> userController.partialUpdate(0L, updates));
    }

    @Test
    void shouldNotPartialUpdateUserWithNotExistId() {
        user = new User();
        user.setName("name");
        user.setLogin("login");
        user.setEmail("my1email@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.createUser(user);

        Map<String, Object> updates = Map.of("name", "secondName");

        assertThrows(NotFoundException.class, () -> userController.partialUpdate(10L, updates));
    }
}
