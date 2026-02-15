-- Очистка таблиц (в обратном порядке из-за foreign keys)
DELETE FROM comments;
DELETE FROM bookings;
DELETE FROM items;
DELETE FROM requests;
DELETE FROM users;

-- Сброс последовательностей
ALTER SEQUENCE users_id_seq RESTART WITH 1;
ALTER SEQUENCE items_id_seq RESTART WITH 1;
ALTER SEQUENCE bookings_id_seq RESTART WITH 1;
ALTER SEQUENCE requests_id_seq RESTART WITH 1;
ALTER SEQUENCE comments_id_seq RESTART WITH 1;

-- Добавление тестовых пользователей
INSERT INTO users (id, name, email) VALUES
(1, 'User1', 'user1@example.com'),
(2, 'User2', 'user2@example.com'),
(3, 'User3', 'user3@example.com');

-- Добавление тестовых items
INSERT INTO items (id, name, description, is_available, owner_id) VALUES
(1, 'Drill', 'Powerful electric drill', true, 1),
(2, 'Hammer', 'Heavy duty hammer', true, 1),
(3, 'Saw', 'Circular saw', false, 2);