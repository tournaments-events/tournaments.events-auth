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

CREATE TABLE provider_user_info
(
    provider_id           text NOT NULL,
    user_id               text NOT NULL,

    name                  text,
    given_name            text,
    family_name           text,
    middle_name           text,
    nickname              text,

    prefered_username     text,
    profile               text,
    picture               text,
    website               text,

    email                 text,
    email_verified        boolean,

    gender                text,
    birth_date            date,

    zone_info             text,
    locale                text,

    phone_number          text,
    phone_number_verified boolean,

    last_update_date      timestamp,
    PRIMARY KEY (provider_id, user_id)
);

CREATE INDEX provider_user_info__user_id ON provider_user_info (user_id);

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

-- Cryptographic keys table

CREATE TABLE crypto_keys
(
    name               text      NOT NULL,
    algorithm          text      NOT NULL,

    public_key         bytea,
    public_key_format  text,

    private_key        bytea     NOT NULL,
    private_key_format text      NOT NULL,

    creation_date      timestamp NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE indexed_crypto_keys
(
    name               text      NOT NULL,
    index              SERIAL,
    algorithm          text      NOT NULL,

    public_key         bytea,
    public_key_format  text,

    private_key        bytea     NOT NULL,
    private_key_format text      NOT NULL,

    creation_date      timestamp NOT NULL,
    PRIMARY KEY (name, index)
);

CREATE INDEX indexed_crypto_keys__name ON indexed_crypto_keys (name);
