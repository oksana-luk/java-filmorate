package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getUsers();

    Optional<User> findUserById(Long id);

    User addUser(User user);

    User updateUser(User user);

    boolean deleteUserById(Long id);

    boolean containsEmail(String email);

    boolean containsFriend(Long id, Long friendId);

    void addFriend(Long id, Long friendId);

    boolean deleteFriend(Long id, Long friendId);

    Collection<User> getFriends(Long id);

    Collection<User> getCommonFriends(Long id, Long otherId);
}
