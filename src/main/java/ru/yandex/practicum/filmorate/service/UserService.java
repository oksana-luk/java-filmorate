package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.BaseUserDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;
    @Autowired
    public UserService(@Qualifier("userRepository") UserStorage userStorage, @Qualifier("filmRepository") FilmStorage filmStorage, FeedService feedService) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedService = feedService;

    }

    public Collection<UserDto> findAll() {
        return userStorage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        User user = validateNotFound(id);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto createUser(NewUserRequest newUserRequest) {
        validateEmail(newUserRequest);
        if (newUserRequest.getLogin().contains(" ")) {
            setLogWarn("Login should not contain space");
            throw new ValidationException(newUserRequest.getLogin(), "Login should not contain space");
        }
        if (Objects.isNull(newUserRequest.getName()) || newUserRequest.getName().isBlank()) {
            newUserRequest.setName(newUserRequest.getLogin());
        }
        User user = UserMapper.mapToUser(newUserRequest);
        user = userStorage.addUser(user);
        return UserMapper.mapToUserDto(validateNotFound(user.getId()));
    }

    public UserDto updateUser(UpdateUserRequest updateUserRequest) {
        User user = validateNotFound(updateUserRequest.getId());
        validateLogin(updateUserRequest);
        if (!user.getEmail().equals(updateUserRequest.getEmail())) {
            validateEmail(updateUserRequest);
        }
        user = UserMapper.updateUserFields(user, updateUserRequest);
        user = userStorage.updateUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public boolean deleteUserById(Long id) {
        validateNotFound(id);
        return userStorage.deleteUserById(id);
    }

    public void addFriend(Long id, Long friendId) {
        validateNotFound(id);
        validateNotFound(friendId);
        validateAddFriend(id, friendId);
        userStorage.addFriend(id, friendId);
        feedService.addEvent(id, EventType.FRIEND, EventOperation.ADD, friendId);
        log.info("Событие добавлено в ленту: пользовател с id: {} добавил друга с id: {}", id, friendId);
    }

    public boolean deleteFriend(Long id, Long friendId) {
        validateNotFound(id);
        validateNotFound(friendId);
        feedService.addEvent(id, EventType.FRIEND, EventOperation.REMOVE, friendId);
        log.info("Событие добавлено в ленту: пользовател с id: {} удалил друга с id: {}", id, friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<UserDto> getFriends(Long id) {
        validateNotFound(id);
        return userStorage.getFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        validateNotFound(id);
        validateNotFound(otherId);
        return userStorage.getCommonFriends(id, otherId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> getRecommendations(Long id) {
        validateNotFound(id);
        Optional<User> userOpt = userStorage.getUserWithMaxIntersections(id);
        return userOpt.map(user -> filmStorage.getRecommendations(id, user.getId())
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList())).orElseGet(List::of);
    }

    private User validateNotFound(Long id) {
        Optional<User> userOpt = userStorage.findUserById(id);
        if (userOpt.isEmpty()) {
            String message = String.format("The service did not find user by id %s", id);
            setLogWarn(message);
            throw new NotFoundException(message);
        } else {
            return userOpt.get();
        }
    }

    private void validateEmail(BaseUserDto user) {
        if (userStorage.containsEmail(user.getEmail())) {
            String message = String.format("Email %s is already taken", user.getEmail());
            setLogWarn(message);
            throw new DuplicateException(message);
        }
    }

    private void validateLogin(BaseUserDto user) {
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
