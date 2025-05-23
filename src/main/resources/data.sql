MERGE INTO genres (genre_id, name)
KEY (genre_id) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

MERGE INTO ratings (rating_id, name)
KEY (rating_id) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

INSERT INTO directors (name) SELECT 'Истомин Олег'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Истомин Олег');

INSERT INTO directors (name) SELECT 'Джон Траволта'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Джон Траволта');

INSERT INTO directors (name) SELECT 'Лев Толстой'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Лев Толстой');

INSERT INTO directors (name) SELECT 'Сергей Лукьяненко'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Сергей Лукьяненко');

INSERT INTO directors (name) SELECT 'Алексей Иванов'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Алексей Иванов');

INSERT INTO directors (name) SELECT 'Антон Чехов'
WHERE NOT EXISTS (SELECT name FROM directors WHERE name = 'Антон Чехов');

