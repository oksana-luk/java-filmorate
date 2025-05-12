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
        FOREIGN KEY (user_id) REFERENCES users(user_id),
        FOREIGN KEY (friend_id) REFERENCES users(user_id)
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
        FOREIGN KEY (rating_id) REFERENCES ratings(rating_id)
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
        FOREIGN KEY (film_id) REFERENCES films(film_id),
        FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
        );

        CREATE TABLE IF NOT EXISTS likes (
        like_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        film_id BIGINT,
        user_id BIGINT,
        UNIQUE(film_id, user_id),
        FOREIGN KEY (film_id) REFERENCES films(film_id),
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        );
