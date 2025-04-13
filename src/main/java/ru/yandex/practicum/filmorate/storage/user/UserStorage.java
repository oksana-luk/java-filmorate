package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    Optional<User> findUserById(Long id);

    User addUser(User user);

    User updateUser(User User);

    User deleteUserById(Long id);

    Long getNextId();

    boolean containsEmail(User user);

    boolean containsFriend(Long id, Long friendId);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);
}
