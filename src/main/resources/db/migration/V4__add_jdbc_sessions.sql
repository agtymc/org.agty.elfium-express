CREATE TABLE IF NOT EXISTS spring_users_session (
    primary_id char(36) NOT NULL,
    session_id char(36) NOT NULL,
    creation_time bigint NOT NULL,
    last_access_time bigint NOT NULL,
    max_inactive_interval integer NOT NULL,
    expiry_time bigint NOT NULL,
    principal_name varchar(100),
    CONSTRAINT spring_users_session_pk PRIMARY KEY (primary_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS spring_users_session_ix1
    ON spring_users_session (session_id);

CREATE INDEX IF NOT EXISTS spring_users_session_ix2
    ON spring_users_session (expiry_time);

CREATE INDEX IF NOT EXISTS spring_users_session_ix3
    ON spring_users_session (principal_name);

CREATE TABLE IF NOT EXISTS spring_users_session_attributes (
    session_primary_id char(36) NOT NULL,
    attribute_name varchar(200) NOT NULL,
    attribute_bytes bytea NOT NULL,
    CONSTRAINT spring_users_session_attributes_pk PRIMARY KEY (session_primary_id, attribute_name),
    CONSTRAINT spring_users_session_attributes_fk
        FOREIGN KEY (session_primary_id) REFERENCES spring_users_session(primary_id) ON DELETE CASCADE
);
