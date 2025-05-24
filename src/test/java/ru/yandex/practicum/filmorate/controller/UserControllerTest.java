package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserService userService = new UserService(userStorage, filmStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldReturnAllUsers() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        UserDto user = userController.createUser(newUserRequest);
        Collection<UserDto> users = userController.findAll();
        assertNotNull(users, "Service didn't return all users.");
        assertFalse(users.isEmpty(), "Service didn't return all users.");
        assertTrue(users.contains(user), "Service didn't return all users.");
        assertEquals(1, users.size(), "Service didn't return all users.");
    }

    @Test
    void shouldAddNewUserAndSetId() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        UserDto user = userController.createUser(newUserRequest);

        Collection<UserDto> users = userController.findAll();
        assertTrue(users.contains(user), "The user was not created or saved.");
        assertEquals(1L, (long) user.getId(), "Incorrect id was set by user.");
    }

    @Test
    void shouldNotAddNewUserWithDuplicateEmail() {
        String duplicateEmail = "first@email.com";
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail(duplicateEmail);
        userController.createUser(newUserRequest);

        NewUserRequest newUserRequest2 = new NewUserRequest();
        newUserRequest2.setName("name2");
        newUserRequest2.setLogin("login2");
        newUserRequest2.setBirthday(LocalDate.of(2002, 2, 2));
        newUserRequest2.setEmail(duplicateEmail);
        assertThrows(DuplicateException.class, () -> userController.createUser(newUserRequest2), "New user with incorrect email was created.");
    }

    @Test
    void shouldNotAddNewUserLoginContainsSpace() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("my login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");

        assertThrows(ValidationException.class, () -> userController.createUser(newUserRequest), "New user with incorrect login with spase was created.");
    }

    @Test
    void shouldAddNewUserWithoutNameAndSetNameFromLogin() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        UserDto user = userController.createUser(newUserRequest);

        assertEquals(newUserRequest.getLogin(), user.getName());
    }

    @Test
    void shouldAddNewUserWithEmptyNameAndSetNameFromLogin() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        UserDto user = userController.createUser(newUserRequest);

        assertEquals(newUserRequest.getLogin(), user.getName());
    }

    @Test
    void shouldAddNewUserWithBlankNameAndSetNameFromLogin() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName(" ");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        UserDto user = userController.createUser(newUserRequest);

        assertEquals(newUserRequest.getLogin(), user.getName());
    }

    @Test
    void shouldNotUpdateUserWithZeroId() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName(" ");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("emai@mail.com");
        userController.createUser(newUserRequest);
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(0L);

        assertThrows(NotFoundException.class, () -> userController.updateUser(updateUserRequest));
    }

    @Test
    void shouldNotUpdateUserWithNotExistId() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(10L);

        assertThrows(NotFoundException.class, () -> userController.updateUser(updateUserRequest));
    }

    @Test
    void shouldNotUpdateUserWithDuplicateEmail() {
        String duplicateEmail = "first@email.com";
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail(duplicateEmail);
        userController.createUser(newUserRequest);

        NewUserRequest newUserRequest2 = new NewUserRequest();
        newUserRequest2.setName("name2");
        newUserRequest2.setLogin("login2");
        newUserRequest2.setBirthday(LocalDate.of(2002, 2, 2));
        newUserRequest2.setEmail("second@mail.com");
        UserDto userDto = userController.createUser(newUserRequest2);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(userDto.getId());
        updateUserRequest.setEmail(duplicateEmail);

        assertThrows(DuplicateException.class, () -> userController.updateUser(updateUserRequest));
    }

    @Test
    void shouldUpdateAllFields() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("first@email.com");
        UserDto createdUser = userController.createUser(newUserRequest);


        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(createdUser.getId());
        updateUserRequest.setName("newName");
        updateUserRequest.setLogin("newLogin");
        updateUserRequest.setEmail("newEmail@mail.com");
        updateUserRequest.setBirthday(LocalDate.of(1999, 1, 1));
        UserDto updatedUser = userController.updateUser(updateUserRequest);

        assertNotNull(updatedUser);
        assertNotEquals(createdUser.getName(), updatedUser.getName());
        assertNotEquals(createdUser.getLogin(), updatedUser.getLogin());
        assertNotEquals(createdUser.getEmail(), updatedUser.getEmail());
        assertNotEquals(createdUser.getBirthday(), updatedUser.getBirthday());
    }

    @Test
    void shouldUpdate_Login() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("first@email.com");
        UserDto createdUser = userController.createUser(newUserRequest);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(createdUser.getId());
        updateUserRequest.setLogin("newLogin");
        UserDto updatedUser = userController.updateUser(updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), "newLogin");
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void shouldUpdate_Email() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("first@email.com");
        UserDto createdUser = userController.createUser(newUserRequest);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(createdUser.getId());
        updateUserRequest.setEmail("newEmail@mail.com");
        UserDto updatedUser = userController.updateUser(updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), "newEmail@mail.com");
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void shouldUpdate_Birthday() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("first@email.com");
        UserDto createdUser = userController.createUser(newUserRequest);


        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(createdUser.getId());
        updateUserRequest.setEmail("first@email.com");
        updateUserRequest.setBirthday(LocalDate.of(2012, 12, 12));
        UserDto updatedUser = userController.updateUser(updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), createdUser.getName());
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), LocalDate.of(2012, 12, 12));
    }

    @Test
    void shouldUpdate_Name() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("name");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.of(2000, 1, 1));
        newUserRequest.setEmail("first@email.com");
        UserDto createdUser = userController.createUser(newUserRequest);


        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(createdUser.getId());
        updateUserRequest.setName("newName");
        UserDto updatedUser = userController.updateUser(updateUserRequest);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getLogin(), createdUser.getLogin());
        assertEquals(updatedUser.getName(), "newName");
        assertEquals(updatedUser.getEmail(), createdUser.getEmail());
        assertEquals(updatedUser.getBirthday(), createdUser.getBirthday());
    }
}
