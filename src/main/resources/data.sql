-- This scripts will run automatically when app starts to populate db with some test data
INSERT INTO users (username, password, enabled) VALUES
    ('user', '$2a$12$LnpwkCDjIt9xSsmfYsB..e1JPTYWQKErtWIGbDjyWaUP34hD36Ype', true);

INSERT INTO authorities (username, authority) VALUES
    ('user', 'ROLE_USER');
