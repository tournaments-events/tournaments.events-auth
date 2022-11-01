-- To create the database on your local PostgreSQL server
-- CREATE DATABASE tournaments_auth WITH OWNER = DEFAULT;

CREATE TABLE login_attempt
(
    state         text      NOT NULL,
    redirect_uri  text      NOT NULL,
    creation_date timestamp NOT NULL,
    update_date   timestamp NOT NULL,
    PRIMARY KEY (state)
);
