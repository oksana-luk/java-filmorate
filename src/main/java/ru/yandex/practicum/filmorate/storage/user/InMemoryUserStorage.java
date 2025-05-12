package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmail = new HashSet<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        usersEmail.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        usersEmail.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id)).map(User::new);
    }

    @Override
    public boolean deleteUserById(Long id) {
        return users.remove(id) != null;
    }

    public Long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }

    @Override
    public boolean containsEmail(String email) {
        return usersEmail.contains(email);
    }

    @Override
    public boolean containsFriend(Long id, Long friendId) {
        if (!friends.containsKey(id)) {
            return false;
        }
        return friends.get(id).contains(friendId);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        addFriendTo(id, friendId);
        addFriendTo(friendId, id);
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        return deleteFriendFrom(id, friendId) || deleteFriendFrom(friendId, id);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        if (friends.containsKey(id)) {
            return friends.get(id).stream()
                    .map(this::findUserById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> friends1 = friends.get(id);
        Set<Long> friends2 = friends.get(otherId);
        return friends1.stream()
                .filter(friends2::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private void addFriendTo(Long id, Long friendId) {
        if (friends.containsKey(id)) {
            friends.get(id).add(friendId);
        } else {
            Set<Long> setFriends = new HashSet<>();
            setFriends.add(friendId);
            friends.put(id, setFriends);
        }
    }

    private boolean deleteFriendFrom(Long id, Long friendId) {
        if (friends.containsKey(id)) {
           return friends.get(id).remove(friendId);
        } else {
            return false;
        }
    }
}
