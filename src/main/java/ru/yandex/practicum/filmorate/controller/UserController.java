package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.CreateInfo;
import ru.yandex.practicum.filmorate.model.UpdateInfo;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmail = new HashSet<>();
    private static final  Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        log.debug("GET /films: the collection of users were return");
        return users.values();
    }

    @PostMapping
    public User createUser(@Validated(CreateInfo.class) @RequestBody User user) {
        String method = "POST /users";
        if (usersEmail.contains(user.getEmail())) {
            setLogWarn(method, String.format("Email %s is already taken", user.getEmail()));
            throw new ValidationException("Email is already taken");
        }
        if (user.getLogin().contains(" ")) {
            setLogWarn(method, "Login should not contain space");
            throw new ValidationException("Login should not contain space");
        }
        user.setId(getNextId());
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        usersEmail.add(user.getEmail());
        log.debug("{}: the process was completed successfully. A new user {} with id {} was created", method, user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Validated({UpdateInfo.class, CreateInfo.class}) @RequestBody User user) {
        String method = "PUT /users";
        User currentUser = users.get(user.getId());
        if (currentUser == null) {
            String message = String.format("The user with id %d not found", user.getId());
            setLogWarn(method, message);
            throw new NotFoundException(message);
        }
        if (user.getLogin().contains(" ")) {
            setLogWarn(method, "Login should not contain space");
            throw new ValidationException("Login should not contain space");
        }
        if (!currentUser.getEmail().equals(user.getEmail())
                && usersEmail.contains(user.getEmail())) {
            setLogWarn(method, String.format("Email %s is already taken", user.getEmail()));
            throw new ValidationException("Email is already taken");
        }
        users.put(user.getId(), user);
        usersEmail.add(user.getEmail());
        log.debug("{}: the process was completed successfully. A user {} with id {} was updated", method, user.getLogin(), user.getId());
        return user;
    }

    @PatchMapping
    public User partialUpdate(@Validated({UpdateInfo.class, Default.class}) @RequestBody User user) {
        String method = "PUT /users";
        User currentUser = users.get(user.getId());
        if (Objects.isNull(currentUser)) {
            String message = String.format("The user with id %d not found", user.getId());
            setLogWarn(method, message);
            throw new NotFoundException(message);
        }
        if (user.getEmail() != null && !currentUser.getEmail().equals(user.getEmail())
                && usersEmail.contains(user.getEmail())) {
            setLogWarn(method, String.format("Email %s is already taken", user.getEmail()));
            throw new ValidationException("Email is already taken");
        }
        validate(user, method);
        partialUpdate(user, currentUser);
        users.put(currentUser.getId(), currentUser);
        usersEmail.add(currentUser.getEmail());
        log.debug("{}: the process was completed successfully. A user {} with id {} was partial updated", method, currentUser.getLogin(), currentUser.getId());
        return currentUser;
    }

    private void partialUpdate(User sourceUser, User receiveUser) {
        if (!Objects.isNull(sourceUser.getName())) {
            receiveUser.setName(sourceUser.getName());
        }
        if (!Objects.isNull(sourceUser.getEmail())) {
            receiveUser.setEmail(sourceUser.getEmail());
        }
        if (!Objects.isNull(sourceUser.getBirthday())) {
            receiveUser.setBirthday(sourceUser.getBirthday());
        }
        if (!Objects.isNull(sourceUser.getLogin())) {
            receiveUser.setLogin(sourceUser.getLogin());
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(User user, String method) {
        if (user.getLogin() != null && user.getLogin().isBlank()) {
            setLogWarn(method, "Login should not be empty");
            throw new ValidationException("Login should not be empty");
        }
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            setLogWarn(method, "Login should not contain space");
            throw new ValidationException("Login should not contain space");
        }
    }

    private void setLogWarn(String method, String message) {
        log.warn("{}: the process ended with an error. {}", method, message);
    }
}
