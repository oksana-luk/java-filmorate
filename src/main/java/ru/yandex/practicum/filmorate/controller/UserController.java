package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final  Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<UserDto> findAll() {
        Collection<UserDto> users =  userService.findAll();
        log.debug("GET /users: the collection of users has been returned");
        return users;
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        log.debug("GET/users/id: start of finding of user {}", id);
        UserDto userDto = userService.findUserById(id);
        log.debug("GET/users/id: the process was completed successfully. A user {} with id {} has been found", userDto.getLogin(), userDto.getId());
        return userDto;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.debug("POST/users: start of creating of new user {}", newUserRequest.getLogin());
        UserDto createdUser = userService.createUser(newUserRequest);
        log.debug("POST/users: the process was completed successfully. A new user {} with id {} has been created",
                createdUser.getLogin(), createdUser.getId());
        return createdUser;
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.debug("PUT/users: start of updating of user {}", updateUserRequest.getLogin());
        UserDto updatedUser = userService.updateUser(updateUserRequest);
        log.debug("PUT/users: the process was completed successfully. A user {} with id {} has been updated", updatedUser.getLogin(), updatedUser.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        log.debug("DELETE/users/id: start of deleting of movie with id {}", id);
        userService.deleteUserById(id);
        log.debug("DELETE/users/id: the process was completed successfully. A user with id {} has been deleted", id);
        return ResponseEntity.ok(Map.of("result",String.format("User with id %s has been deleted successfully.", id)));
    }

    @PutMapping("{id}/friends/{friendId}")
    public ResponseEntity<Map<String, String>> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("PUT/friends/friendId: start of adding new friend with id {} to user with id {}", friendId, id);
        userService.addFriend(id, friendId);
        log.debug("PUT/friends/friendId:: the process was completed successfully. A user with id {} has been added as friend to user with id {}", friendId, id);
        return ResponseEntity.ok(Map.of("result",String.format("User with id %s has been successfully added as a friend.", friendId)));
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public ResponseEntity<Map<String, String>> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.debug("DELETE/friends/friendId: start of deleting friend with id {} of user with id {}", friendId, id);
        userService.deleteFriend(id, friendId);
        log.debug("DELETE/friends/friendId: the process was completed successfully. A user with id {} has been deleted from friends of user with id {}", friendId, id);
        return ResponseEntity.ok(Map.of("result",String.format("User with id %s has been successfully deleted from friends.", friendId)));
    }

    @GetMapping("{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable Long id) {
        Collection<UserDto> friends = userService.getFriends(id);
        log.debug("GET /users/id/friends: the collection of friends of user has been returned");
        return friends;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Collection<UserDto> friends = userService.getCommonFriends(id, otherId);
        log.debug("GET /users/id/friends/common/id: the collection of common friends of users has been returned");
        return friends;
    }

    @GetMapping("{id}/recommendations")
    public Collection<FilmDto> getCommonFriends(@PathVariable Long id) {
        Collection<FilmDto> films = userService.getRecommendations(id);
        log.debug("GET /users/id/recommendations: the collection of recommended films for user has been returned");
        return films;
    }
}

