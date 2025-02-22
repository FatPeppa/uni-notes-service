/* Create Schema */

--CREATE SCHEMA IF NOT EXISTS public;

/* Create Tables */

CREATE TABLE IF NOT EXISTS "category"
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(512) NULL,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS media_metadata
(
    id uuid NOT NULL,
    user_id bigint NOT NULL,
    name varchar(512) NOT NULL,
    file_type varchar(40) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    url varchar(1024) NOT NULL,
    CONSTRAINT PK_media_metadata PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS media_type
(
    code varchar(40) NOT NULL,
    CONSTRAINT PK_media_type PRIMARY KEY (code)
);

CREATE TABLE IF NOT EXISTS note
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    category_id bigint NULL,
    name varchar(255) NOT NULL,
    media_id uuid NOT NULL,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_note PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS note_media
(
    note_id bigint NOT NULL,
    media_id uuid NOT NULL,
    created_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_note_media PRIMARY KEY (note_id, media_id)
);

CREATE TABLE IF NOT EXISTS note_tag
(
    note_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_note_tag PRIMARY KEY (note_id, tag_id)
);

CREATE TABLE IF NOT EXISTS t_deactivated_token
(
    id uuid NOT NULL,
    token varchar NOT NULL,
    created_date timestamp without time zone NOT NULL,
    CONSTRAINT PK_t_deactivated_token PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tag
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name varchar(50) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_tag PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id bigint NOT NULL,
    username varchar(30) NOT NULL,
    password varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    role varchar(40) NOT NULL,
    register_date timestamp with time zone NOT NULL,
    last_logon_date timestamp with time zone NOT NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);

/* Create Indexes */

CREATE INDEX IF NOT EXISTS "IXFK_category_users"
    ON category
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_user_id_name"
    ON category
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_media_metadata_media_type"
    ON media_metadata
    USING btree(file_type ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_media_metadata_users"
    ON media_metadata
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_user_id_name"
    ON media_metadata
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "IX_user_id_file_type_name"
    ON media_metadata
    USING btree(user_id ASC, file_type ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_category"
    ON note
    USING btree(category_id ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_users"
    ON note
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_user_id_name"
    ON note
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "IX_user_id_category_id_name"
    ON note
    USING btree(user_id ASC, category_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_media_metadata"
    ON note_media
        USING btree(media_id ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_media_media_metadata"
    ON note_media
    USING btree(media_id ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_media_note"
    ON note_media
    USING btree(note_id ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_tag_note"
    ON note_tag
    USING btree(note_id ASC)
;

CREATE INDEX IF NOT EXISTS "IXFK_note_tag_tag"
    ON note_tag
    USING btree(tag_id ASC)
;


CREATE INDEX IF NOT EXISTS "IXFK_tag_users"
    ON tag
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_user_id_name"
    ON tag
    USING btree(user_id ASC, name ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_username"
    ON users
    USING btree (username ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "UIX_email"
    ON users
    USING btree (email ASC)
;


/* Drop Constraints */

ALTER TABLE category
    DROP CONSTRAINT IF EXISTS FK_category_users;

ALTER TABLE media_metadata
    DROP CONSTRAINT IF EXISTS FK_mediametadata_media_type;

ALTER TABLE media_metadata
    DROP CONSTRAINT IF EXISTS FK_media_metadata_users;

ALTER TABLE note
    DROP CONSTRAINT IF EXISTS FK_note_category;

ALTER TABLE note
    DROP CONSTRAINT IF EXISTS FK_note_users;

ALTER TABLE note_media
    DROP CONSTRAINT IF EXISTS FK_note_media_metadata;

ALTER TABLE note_media
    DROP CONSTRAINT IF EXISTS FK_note_media_media_metadata;

ALTER TABLE note_media
    DROP CONSTRAINT IF EXISTS FK_note_media_note;

ALTER TABLE note_tag
    DROP CONSTRAINT IF EXISTS FK_note_tag_note;

ALTER TABLE note_tag
    DROP CONSTRAINT IF EXISTS FK_note_tag_tag;

ALTER TABLE tag
    DROP CONSTRAINT IF EXISTS FK_tag_users;

/* Create Foreign Key Constraints */

ALTER TABLE category
    ADD CONSTRAINT "FK_category_users"
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE media_metadata
    ADD CONSTRAINT "FK_media_metadata_media_type"
    FOREIGN KEY (file_type)
    REFERENCES media_type (code)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE media_metadata
    ADD CONSTRAINT "FK_media_metadata_users"
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE note
    ADD CONSTRAINT "FK_note_category"
    FOREIGN KEY (category_id)
    REFERENCES category (id)
    ON DELETE Set Null
    ON UPDATE Cascade
;

ALTER TABLE note
    ADD CONSTRAINT "FK_note_users"
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE note
    ADD CONSTRAINT "FK_note_media_metadata"
    FOREIGN KEY (media_id)
    REFERENCES media_metadata (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE note_media
    ADD CONSTRAINT "FK_note_media_media_metadata"
    FOREIGN KEY (media_id)
    REFERENCES media_metadata (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE note_media
    ADD CONSTRAINT "FK_note_media_note"
    FOREIGN KEY (note_id)
    REFERENCES note (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE note_tag
    ADD CONSTRAINT "FK_note_tag_note"
    FOREIGN KEY (note_id)
    REFERENCES note (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE note_tag
    ADD CONSTRAINT "FK_note_tag_tag"
    FOREIGN KEY (tag_id)
    REFERENCES tag (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE tag
    ADD CONSTRAINT "FK_tag_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

/* Create Table Comments, Sequences for Autonumber Columns */

CREATE SEQUENCE IF NOT EXISTS users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS category_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS note_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS tag_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;


