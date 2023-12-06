-- To create the database on your local PostgreSQL server
-- CREATE DATABASE tournaments_auth WITH OWNER = DEFAULT;

-- User-related tables

CREATE TABLE users
(
    id        uuid    NOT NULL DEFAULT gen_random_uuid(),
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
    id                uuid      NOT NULL DEFAULT gen_random_uuid(),
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

-- JWT table

CREATE TABLE jwt_keys
(
    name          text      NOT NULL,
    algorithm     text      NOT NULL,

    public_key    bytea     NOT NULL,
    private_key   bytea     NOT NULL,

    creation_date timestamp NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE indexed_jwt_keys
(
    name          text      NOT NULL,
    index         SERIAL,
    algorithm     text      NOT NULL,

    public_key    bytea     NOT NULL,
    private_key   bytea     NOT NULL,

    creation_date timestamp NOT NULL,
    PRIMARY KEY (name, index)
);

CREATE INDEX indexed_jwt_keys__name ON indexed_jwt_keys (name);
