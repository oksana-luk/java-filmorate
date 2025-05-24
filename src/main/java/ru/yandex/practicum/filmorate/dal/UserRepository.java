package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@Component("userRepository")
public class UserRepository extends BaseRepository<User> implements UserStorage {
    protected final UserRowMapper mapper;

    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, birthday, name) VALUES (?,?,?,?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, birthday = ?, name = ? WHERE user_id = ?";
    private static final String DELETE_USER_QUERY = "DELETE users WHERE user_id = ?";
    private static final String INSERT_USER_FRIEND_QUERY = "INSERT INTO user_friends (user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_USER_FRIEND_BY_ID_QUERY = """
                                                                SELECT u.*
                                                                FROM users u
                                                                JOIN user_friends uf ON u.user_id = uf.friend_id
                                                                WHERE uf.user_id = ? AND uf.friend_id = ?;""";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_ALL_USER_FRIENDS_QUERY = """
                                                                SELECT u.*
                                                                FROM users u
                                                                JOIN user_friends uf ON u.user_id = uf.friend_id
                                                                WHERE uf.user_id = ?;""";
    private static final String FIND_COMMON_FRIENDS_QUERY = """
                                                                SELECT u.*
                                                                FROM user_friends f1
                                                                JOIN user_friends f2 ON f1.friend_id = f2.friend_id
                                                                JOIN users u ON f1.friend_id = u.user_id
                                                                WHERE f1.user_id = ? AND f2.user_id = ?;""";
    private static final String FIND_USER_MAX_INTERSECTION_QUERY = """
                                                                SELECT u.*
                                                                FROM likes l
                                                                LEFT JOIN users AS u ON l.user_id = u.user_id
                                                                WHERE l.film_id IN (SELECT l.film_id
                                                                                    FROM likes l
                                                                                    WHERE l.user_id = ?) AND NOT l.user_id = ?
                                                                GROUP BY l.user_id
                                                                ORDER BY COUNT(l.film_id) DESC
                                                                LIMIT 1
                                                                """;


    @Autowired
    public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc);
        this.mapper = mapper;
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_USERS_QUERY, mapper);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return findOne(FIND_USER_BY_ID_QUERY, mapper, id);
    }

    @Override
    public User addUser(User user) {
        long id = insert(INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName(),
                user.getId());
        return user;
    }

    @Override
    public boolean deleteUserById(Long id) {
        return delete(DELETE_USER_QUERY, id);
    }

    @Override
    public boolean containsEmail(String email) {
        Optional<User> userOpt = findOne(FIND_USER_BY_EMAIL_QUERY, mapper, email);
        return userOpt.isPresent();
    }

    @Override
    public boolean containsFriend(Long id, Long friendId) {
        Optional<User> userOpt = findOne(FIND_USER_FRIEND_BY_ID_QUERY, mapper, id, friendId);
        return userOpt.isPresent();
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        insert(INSERT_USER_FRIEND_QUERY,
                id,
                friendId);
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        return delete(DELETE_USER_FRIEND_QUERY, id, friendId);
    }

    @Override
    public Collection<User> getFriends(Long id) {
        return findMany(FIND_ALL_USER_FRIENDS_QUERY, mapper, id);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long otherId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, mapper, id, otherId);
    }

    @Override
    public Optional<User> getUserWithMaxIntersections(Long id) {
        return findOne(FIND_USER_MAX_INTERSECTION_QUERY, mapper, id, id);
    }
}


