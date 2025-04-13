package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.getUsers();
    }

    public User findUserById(Long id) {
        validateNotFound(id);
        return userStorage.findUserById(id).get();
    }

    public User createUser(User user) {
        validateEmail(user);
        if (user.getLogin().contains(" ")) {
            setLogWarn("Login should not contain space");
            throw new ValidationException(user.getLogin(), "Login should not contain space");
        }
        user.setId(userStorage.getNextId());
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateNotFound(user.getId());
        validateLogin(user);
        User currentUser = userStorage.findUserById(user.getId()).get();
        if (!currentUser.getEmail().equals(user.getEmail())) {
            validateEmail(user);
        }
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User partialUpdate(Long id, Map<String, Object> updates) {
        validateNotFound(id);
        User currentUser = userStorage.findUserById(id).get();
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, key);
            if (field != null && !field.getName().equals("id")) {
                field.setAccessible(true);
                if (field.getName().equals("birthday")) {
                    LocalDate releaseDate = LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    ReflectionUtils.setField(field, currentUser, releaseDate);
                } else if (field.getName().equals("email")) {
                    ReflectionUtils.setField(field, currentUser, value);
                    validateEmail(currentUser);
                } else {
                    ReflectionUtils.setField(field, currentUser, value);
                }
            }
        });
        validateLogin(currentUser);
        return userStorage.updateUser(currentUser);
    }

    public User deleteUserById(Long id) {
        validateNotFound(id);
        return userStorage.deleteUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        validateNotFound(id);
        validateNotFound(friendId);
        validateAddFriend(id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        validateNotFound(id);
        validateNotFound(friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getFriends(Long id) {
        validateNotFound(id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        validateNotFound(id);
        validateNotFound(otherId);
        Collection<User> friends1 = userStorage.getFriends(id);
        Collection<User> friends2 = userStorage.getFriends(otherId);
        Collection<User> commonFriends = new ArrayList<>(friends1);
        commonFriends.retainAll(friends2);
        return commonFriends;
    }

    private void validateNotFound(Long id) {
        if (userStorage.findUserById(id).isEmpty()) {
            String message = String.format("The service did not find user by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        }
    }

    private void validateEmail(User user) {
        if (userStorage.containsEmail(user)) {
            String message = String.format("Email %s is already taken", user.getEmail());
            setLogWarn(message);
            throw new DuplicateException(message);
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin() != null && user.getLogin().isBlank()) {
            setLogWarn("Login should not be empty");
            throw new ValidationException(user.getLogin(), "Login should not be empty");
        }
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            setLogWarn("Login should not contain space");
            throw new ValidationException(user.getLogin(), "Login should not contain space");
        }
    }

    private void validateAddFriend(Long id, Long friendId) {
        if (Objects.equals(id, friendId)) {
            String message = String.format("User with id %s cannot be his own friend", friendId);
            setLogWarn(message);
            throw new ValidationException(friendId.toString(), message);
        }
        if (userStorage.containsFriend(id, friendId)) {
            String message = String.format("User with id %s is already friend of user with id %s", friendId, id);
            setLogWarn(message);
            throw new DuplicateException(message);
        }
    }

    private void setLogWarn(String message) {
        log.warn("The process ended with an error. {}", message);
    }
}
