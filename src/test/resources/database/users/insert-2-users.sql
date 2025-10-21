INSERT INTO users(id, email, first_name, last_name, password)
VALUES
(3, 'test@gmail.com', 'test', 'test', '123456'),
(4, 'test2@gmail.com', 'test2', 'test2', '123456');

INSERT INTO users_roles(user_id, role_id)
VALUES
(3, 2),
(4, 2);