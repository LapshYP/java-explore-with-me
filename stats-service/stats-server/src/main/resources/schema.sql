CREATE TABLE IF NOT EXISTS hits
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app      varchar(128)                            NOT NULL,
    uri      varchar(128)                            NOT NULL,
    ip      varchar(128)                            NOT NULL,
    datetime   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
    );
