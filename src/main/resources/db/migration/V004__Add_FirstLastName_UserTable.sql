ALTER TABLE chat_user
    DROP COLUMN full_name;

ALTER TABLE chat_user
    ADD COLUMN first_name VARCHAR(127) NOT NULL;

ALTER TABLE chat_user
    ADD COLUMN last_name VARCHAR(127) NOT NULL;
