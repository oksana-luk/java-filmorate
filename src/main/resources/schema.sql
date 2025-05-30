--
--DROP TABLE IF EXISTS reviews_ratings;
--DROP TABLE IF EXISTS reviews;
--DROP TABLE IF EXISTS eventy;
--DROP TABLE IF EXISTS likes;
--DROP TABLE IF EXISTS director_film;
--DROP TABLE IF EXISTS film_genres;
--DROP TABLE IF EXISTS directors;
--DROP TABLE IF EXISTS films;
--DROP TABLE IF EXISTS genres;
--DROP TABLE IF EXISTS user_friends;
--DROP TABLE IF EXISTS users;
--DROP TABLE IF EXISTS ratings;

CREATE TABLE IF NOT EXISTS users (
        user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        login VARCHAR(50) NOT NULL,
        birthday DATE NOT NULL,
        name VARCHAR(100) NOT NULL
        );

        CREATE TABLE IF NOT EXISTS user_friends (
        user_friend_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        user_id BIGINT,
        friend_id BIGINT,
        UNIQUE(user_id, friend_id),
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
        FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS ratings (
        rating_id SMALLINT PRIMARY KEY,
        name VARCHAR(50) NOT NULL
        );

        CREATE TABLE IF NOT EXISTS films (
        film_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        description VARCHAR(200) NOT NULL,
        release_date DATE NOT NULL,
        duration INT,
        rating_id SMALLINT NOT NULL,
        FOREIGN KEY (rating_id) REFERENCES ratings(rating_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS genres (
        genre_id SMALLINT PRIMARY KEY,
        name VARCHAR(50) NOT NULL
        );

        CREATE TABLE IF NOT EXISTS film_genres (
        film_genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        film_id BIGINT,
        genre_id SMALLINT,
        UNIQUE(film_id, genre_id),
        FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
        FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
        );

        CREATE TABLE IF NOT EXISTS directors (
            id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            name VARCHAR NOT NULL CHECK (TRIM(name) <> '')
        );

        CREATE TABLE IF NOT EXISTS director_film (
            director_id BIGINT REFERENCES directors(id) ON DELETE CASCADE,
            film_id BIGINT REFERENCES films(film_id) ON DELETE CASCADE,
            PRIMARY KEY(director_id, film_id)
        );

        CREATE TABLE IF NOT EXISTS likes (
        like_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        film_id BIGINT,
        user_id BIGINT,
        UNIQUE(film_id, user_id),
        FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS eventy (
        timestamp BIGINT NOT NULL,
        user_Id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
        eventType VARCHAR(50) NOT NULL,
        operation VARCHAR(50) NOT NULL,
        event_Id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        entity_Id BIGINT NOT NULL
        );

        CREATE TABLE IF NOT EXISTS reviews (
            id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
            content VARCHAR NOT NULL CHECK (TRIM(content) <> ''),
            is_positive BOOLEAN NOT NULL,
            user_id BIGINT NOT NULL,
            film_id BIGINT NOT NULL,
            useful INT DEFAULT 0,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS reviews_ratings (
            review_id BIGINT REFERENCES reviews(id) ON DELETE CASCADE,
            user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
            rating_value INT NOT NULL CHECK (rating_value IN (-1, 1)),
            PRIMARY KEY (review_id, user_id)
        );
