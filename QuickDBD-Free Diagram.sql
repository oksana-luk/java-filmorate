-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/iltaCM
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.

-- Modify this code to update the DB schema diagram.
-- To reset the sample schema, replace everything with
-- two dots ('..' - without quotes).

CREATE TABLE "user" (
    "user_id" bigint   NOT NULL,
    "email" varchar(255)   NOT NULL,
    "login" varchar(50)   NOT NULL,
    "birthday" date   NOT NULL,
    "name" varchar(100)   NOT NULL,
    CONSTRAINT "pk_user" PRIMARY KEY (
        "user_id"
     ),
    CONSTRAINT "uc_user_email" UNIQUE (
        "email"
    )
);

CREATE TABLE "user_friend" (
    "user_friend_id" int   NOT NULL,
    "user_id" bigint   NOT NULL,
    "friend_id" bigint   NOT NULL,
    "UNIQIE(user_id," friend_id)   NOT NULL,
    CONSTRAINT "pk_user_friend" PRIMARY KEY (
        "user_friend_id"
     )
);

CREATE TABLE "movie" (
    "film_id" bigint   NOT NULL,
    "name" varchar(100)   NOT NULL,
    "description" varchar(200)   NOT NULL,
    "release_datev" date   NOT NULL,
    "duration" int   NOT NULL,
    "rating_id" smallint   NOT NULL,
    CONSTRAINT "pk_movie" PRIMARY KEY (
        "film_id"
     )
);

CREATE TABLE "rating" (
    "rating_id" smallint   NOT NULL,
    "name" varchar(10)   NOT NULL,
    CONSTRAINT "pk_rating" PRIMARY KEY (
        "rating_id"
     )
);

CREATE TABLE "genre" (
    "genre_id" smallint   NOT NULL,
    "name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_genre" PRIMARY KEY (
        "genre_id"
     )
);

CREATE TABLE "film_genre" (
    "film_genre_id" int   NOT NULL,
    "film_id" bigint   NOT NULL,
    "genre_id" smallint   NOT NULL,
    "UNIQUE(film_id," genre_id)   NOT NULL,
    CONSTRAINT "pk_film_genre" PRIMARY KEY (
        "film_genre_id"
     )
);

CREATE TABLE "like" (
    "like_id" int   NOT NULL,
    "film_id" bigint   NOT NULL,
    "user_id" bigint   NOT NULL,
    "UNIQUE(film_id," user_id)   NOT NULL,
    CONSTRAINT "pk_like" PRIMARY KEY (
        "like_id"
     )
);

ALTER TABLE "user_friend" ADD CONSTRAINT "fk_user_friend_user_id" FOREIGN KEY("user_id")
REFERENCES "user" ("user_id");

ALTER TABLE "user_friend" ADD CONSTRAINT "fk_user_friend_friend_id" FOREIGN KEY("friend_id")
REFERENCES "user" ("user_id");

ALTER TABLE "movie" ADD CONSTRAINT "fk_movie_rating_id" FOREIGN KEY("rating_id")
REFERENCES "rating" ("rating_id");

ALTER TABLE "film_genre" ADD CONSTRAINT "fk_film_genre_film_id" FOREIGN KEY("film_id")
REFERENCES "movie" ("film_id");

ALTER TABLE "film_genre" ADD CONSTRAINT "fk_film_genre_genre_id" FOREIGN KEY("genre_id")
REFERENCES "genre" ("genre_id");

ALTER TABLE "like" ADD CONSTRAINT "fk_like_film_id" FOREIGN KEY("film_id")
REFERENCES "movie" ("film_id");

ALTER TABLE "like" ADD CONSTRAINT "fk_like_user_id" FOREIGN KEY("user_id")
REFERENCES "user" ("user_id");

