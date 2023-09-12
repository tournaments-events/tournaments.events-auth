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
    state         text      NOT NULL,
    redirect_uri  text      NOT NULL,
    creation_date timestamp NOT NULL,
    update_date   timestamp NOT NULL,
    PRIMARY KEY (state)
);
