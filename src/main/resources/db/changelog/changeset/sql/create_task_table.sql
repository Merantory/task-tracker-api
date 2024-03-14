CREATE TABLE task
(
    id            BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    name          VARCHAR                     NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description   VARCHAR                     NOT NULL,
    task_state_id BIGINT                      NOT NULL REFERENCES task_state (id)
);