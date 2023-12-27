-- To create the database on your local PostgreSQL server
-- CREATE DATABASE tournaments_auth WITH OWNER = DEFAULT;

-- User-related tables

CREATE TABLE users
(
    id            uuid      NOT NULL DEFAULT gen_random_uuid(),
    email         text,
    password      text,

    creation_date timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE collected_user_info
(
    user_id               uuid      NOT NULL,
    creation_date         timestamp NOT NULL,
    update_date           timestamp NOT NULL,
    collected_bits        bytea     NOT NULL,

    name                  text,
    given_name            text,
    family_name           text,
    middle_name           text,
    nickname              text,

    preferred_username    text,
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
    PRIMARY KEY (user_id)
);

-- Provider-related tables

CREATE TABLE provider_user_info
(
    provider_id           text      NOT NULL,
    user_id               uuid      NOT NULL,
    fetch_date            timestamp NOT NULL,
    change_date           timestamp NOT NULL,

    subject               text      NOT NULL,

    name                  text,
    given_name            text,
    family_name           text,
    middle_name           text,
    nickname              text,

    preferred_username    text,
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

    updated_at            timestamp,
    PRIMARY KEY (provider_id, user_id)
);

CREATE INDEX provider_user_info__user_id ON provider_user_info (user_id);

-- Auth table

CREATE TABLE authorize_attempts
(
    id              uuid      NOT NULL DEFAULT gen_random_uuid(),
    client_id       text      NOT NULL,
    redirect_uri    text      NOT NULL,
    scope_tokens    text[]    NOT NULL,
    state           text,

    user_id         uuid,

    attempt_date    timestamp NOT NULL,
    expiration_date timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX authorize_attempts__state ON authorize_attempts (state);

CREATE TABLE authorization_codes
(
    attempt_id      uuid      NOT NULL,
    code            text      NOT NULL,
    creation_date   timestamp NOT NULL,
    expiration_date timestamp NOT NULL,
    PRIMARY KEY (attempt_id)
);

CREATE INDEX authorization_codes__code ON authorization_codes (code);

CREATE TABLE authentication_tokens
(
    id                   uuid      NOT NULL DEFAULT gen_random_uuid(),
    type                 text      NOT NULL,
    user_id              uuid      NOT NULL,
    client_id            text      NOT NULL,
    scope_tokens         text[]    NOT NULL,
    authorize_attempt_id uuid      NOT NULL,

    revoked              boolean   NOT NULL,
    issue_date           timestamp NOT NULL,
    expiration_date      timestamp,
    PRIMARY KEY (id)
);

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
    index              SERIAL    NOT NULL,
    algorithm          text      NOT NULL,

    public_key         bytea,
    public_key_format  text,

    private_key        bytea     NOT NULL,
    private_key_format text      NOT NULL,

    creation_date      timestamp NOT NULL,
    PRIMARY KEY (name, index)
);

CREATE INDEX indexed_crypto_keys__name ON indexed_crypto_keys (name);
