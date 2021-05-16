CREATE TABLE T_DB_GLOBAL_TRANS
(
    gtid                  VARCHAR(128)    PRIMARY KEY,
    start_time             VARCHAR(64)     NOT NULL,
    end_time             VARCHAR(128)    NOT NULL,
    status              BOOLEAN         NOT NULL
);