package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.CreateInfo;
import ru.yandex.practicum.filmorate.model.UpdateInfo;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private static final  Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        Collection<User> users =  userService.findAll();
        log.debug("GET /users: the collection of users has been returned");
        return users;
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Long id) {
        log.debug("GET/users/id: start of finding of user {}", id);
        User user = userService.findUserById(id);
        log.debug("GET/users/id: the process was completed successfully. A user {} with id {} has been found", user.getLogin(), user.getId());
        return user;
    }

    @PostMapping
    public User createUser(@Validated(CreateInfo.class) @RequestBody User user) {
        log.debug("POST/users: start of creating of new user {}", user.getLogin());
        User createdUser = userService.createUser(user);
        log.debug("POST/users: the process was completed successfully. A new user {} with id {} has been created",
                createdUser.getLogin(), createdUser.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Validated({UpdateInfo.class, CreateInfo.class}) @RequestBody User user) {
        log.debug("PUT/users: start of updating of user {}", user.getLogin());
        User updatedUser = userService.updateUser(user);
        log.debug("PUT/users: the process was completed successfully. A user {} with id {} has been updated", updatedUser.getLogin(), updatedUser.getId());
        return updatedUser;
    }

    @PatchMapping("/{id}")
    public User partialUpdate(@Validated({UpdateInfo.class, Default.class})
                                    @PathVariable Long id,
                                    @RequestBody Map<String, Object> updates) {
        log.debug("PATCH/users/id: start of partial updating of user with id {}", id);
        User updatedUser = userService.partialUpdate(id, updates);
        log.debug("PATCH/users/id: the process was completed successfully. A user {} with id {} has been updated",
                updatedUser.getLogin(), updatedUser.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFilm(@PathVariable Long id) {
        log.debug("DELETE/users/id: start of deleting of movie with id {}", id);
        User user = userService.deleteUserById(id);
        log.debug("DELETE/users/id: the process was completed successfully. A user {} with id {} has been deleted", user.getLogin(), id);
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
    public Collection<User> getFriends(@PathVariable Long id) {
        Collection<User> friends = userService.getFriends(id);
        log.debug("GET /users/id/friends: the collection of friends of user has been returned");
        return friends;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Collection<User> friends = userService.getCommonFriends(id, otherId);
        log.debug("GET /users/id/friends/common/id: the collection of common friends of users has been returned");
        return friends;
    }

}

