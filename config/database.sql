-- To create the database on your local PostgreSQL server
-- CREATE DATABASE tournaments_auth WITH OWNER = DEFAULT;

-- User-related tables

CREATE TABLE users
(
    id        uuid    NOT NULL,
    username  text    NOT NULL,

    firstname text,
    lastname  text,
    email     text,

    password  text,
    is_admin  boolean NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX users__email ON users (email);

-- OAuth2 table

CREATE TABLE authorize_attempts
(
    id                uuid      NOT NULL,
    client_id         text      NOT NULL,
    redirect_uri      text      NOT NULL,

    client_ip         text,
    client_user_agent text,
    client_referer    text,
    client_state      text,

    attempt_date      timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX authorize_attempts__client_state ON authorize_attempts (client_state);
