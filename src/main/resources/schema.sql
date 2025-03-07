/* Create Schema */

CREATE SCHEMA IF NOT EXISTS public;

/* Create Tables */

CREATE TABLE IF NOT EXISTS public."category"
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(512) NULL,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.media_metadata
(
    id uuid NOT NULL,
    user_id bigint NOT NULL,
    name varchar(512) NOT NULL,
    file_type varchar(40) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    url varchar(1024) NOT NULL,
    CONSTRAINT pk_media_metadata PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.media_type
(
    code varchar(40) NOT NULL,
    CONSTRAINT pk_media_type PRIMARY KEY (code)
);

CREATE TABLE IF NOT EXISTS public.note
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    category_id bigint NULL,
    name varchar(255) NOT NULL,
    text_extraction varchar(512),
    media_id uuid,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT pk_note PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.note_media
(
    note_id bigint NOT NULL,
    media_id uuid NOT NULL,
    created_date timestamp with time zone NOT NULL,
    CONSTRAINT pk_note_media PRIMARY KEY (note_id, media_id)
);

CREATE TABLE IF NOT EXISTS public.note_tag
(
    id bigint NOT NULL,
    note_id bigint NOT NULL,
    tag_id bigint NOT NULL,
    created_date timestamp with time zone NOT NULL,
    CONSTRAINT pk_note_tag PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.t_deactivated_token
(
    id uuid NOT NULL,
    token varchar NOT NULL,
    created_date timestamp without time zone NOT NULL,
    CONSTRAINT pk_t_deactivated_token PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.tag
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name varchar(50) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    last_change_date timestamp with time zone NOT NULL,
    CONSTRAINT pk_tag PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.users
(
    id bigint NOT NULL,
    username varchar(30) NOT NULL,
    password varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    role varchar(40) NOT NULL,
    register_date timestamp with time zone NOT NULL,
    last_logon_date timestamp with time zone NOT NULL,
    blocked boolean DEFAULT FALSE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.user_properties
(
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    last_notes_created_date_sort_direction varchar(4) DEFAULT 'DESC' NOT NULL,
    CONSTRAINT pk_user_properties PRIMARY KEY (id)
);

/* Create Indexes */

CREATE INDEX IF NOT EXISTS "ixfk_category_users"
    ON public.category
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_user_id_name_category"
    ON public.category
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_media_metadata_media_type"
    ON public.media_metadata
    USING btree(file_type ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_media_metadata_users"
    ON public.media_metadata
    USING btree(user_id ASC)
;

CREATE INDEX IF NOT EXISTS "ix_user_id_name_media_metadata"
    ON public.media_metadata
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "ix_user_id_file_type_name"
    ON public.media_metadata
    USING btree(user_id ASC, file_type ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_category"
    ON public.note
    USING btree(category_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_users"
    ON public.note
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_user_id_name_note"
    ON public.note
    USING btree(user_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "ix_user_id_category_id_name"
    ON public.note
    USING btree(user_id ASC, category_id ASC, name ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_media_metadata"
    ON public.note_media
    USING btree(media_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_media_media_metadata"
    ON public.note_media
    USING btree(media_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_media_note"
    ON public.note_media
    USING btree(note_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_tag_note"
    ON public.note_tag
    USING btree(note_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_note_id_tag_id_note_tag"
    ON public.note_tag
    USING btree(note_id ASC, tag_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_note_tag_tag"
    ON public.note_tag
    USING btree(tag_id ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_tag_users"
    ON public.tag
    USING btree(user_id ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_user_id_name_tag"
    ON public.tag
    USING btree(user_id ASC, name ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_username"
    ON public.users
    USING btree (username ASC)
;

CREATE UNIQUE INDEX IF NOT EXISTS "uix_email"
    ON public.users
    USING btree (email ASC)
;

CREATE INDEX IF NOT EXISTS "ixfk_user_properties_users"
    ON public.user_properties
    USING btree(user_id ASC)
;

/* Drop Constraints */

ALTER TABLE public.category
    DROP CONSTRAINT IF EXISTS "fk_category_users";

ALTER TABLE public.media_metadata
    DROP CONSTRAINT IF EXISTS "fk_media_metadata_media_type";

ALTER TABLE public.media_metadata
    DROP CONSTRAINT IF EXISTS "fk_media_metadata_users";

ALTER TABLE public.media_metadata
    DROP CONSTRAINT IF EXISTS "fk_media_metadata_media_type";

ALTER TABLE public.note
    DROP CONSTRAINT IF EXISTS "fk_note_category";

ALTER TABLE public.note
    DROP CONSTRAINT IF EXISTS "fk_note_users";

ALTER TABLE public.note
    DROP CONSTRAINT IF EXISTS "fk_note_media_metadata";

ALTER TABLE public.note_media
    DROP CONSTRAINT IF EXISTS "fk_note_media_media_metadata";

ALTER TABLE public.note_media
    DROP CONSTRAINT IF EXISTS "fk_note_media_note";

ALTER TABLE public.note_tag
    DROP CONSTRAINT IF EXISTS "fk_note_tag_note";

ALTER TABLE public.note_tag
    DROP CONSTRAINT IF EXISTS "fk_note_tag_tag";

ALTER TABLE public.tag
    DROP CONSTRAINT IF EXISTS "fk_tag_users";

ALTER TABLE public.user_properties
    DROP CONSTRAINT IF EXISTS "fk_user_properties_users";

/* Create Foreign Key Constraints */

ALTER TABLE public.category
    ADD CONSTRAINT "fk_category_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE public.media_metadata
    ADD CONSTRAINT "fk_media_metadata_media_type"
    FOREIGN KEY (file_type)
    REFERENCES public.media_type (code)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE public.media_metadata
    ADD CONSTRAINT "fk_media_metadata_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE public.note
    ADD CONSTRAINT "fk_note_category"
    FOREIGN KEY (category_id)
    REFERENCES public.category (id)
    ON DELETE Set Null
    ON UPDATE Cascade
;

ALTER TABLE public.note
    ADD CONSTRAINT "fk_note_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE public.note
    ADD CONSTRAINT "fk_note_media_metadata"
    FOREIGN KEY (media_id)
    REFERENCES public.media_metadata (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE public.note_media
    ADD CONSTRAINT "fk_note_media_media_metadata"
    FOREIGN KEY (media_id)
    REFERENCES public.media_metadata (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE public.note_media
    ADD CONSTRAINT "fk_note_media_note"
    FOREIGN KEY (note_id)
    REFERENCES public.note (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE public.note_tag
    ADD CONSTRAINT "fk_note_tag_note"
    FOREIGN KEY (note_id)
    REFERENCES public.note (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE public.note_tag
    ADD CONSTRAINT "fk_note_tag_tag"
    FOREIGN KEY (tag_id)
    REFERENCES public.tag (id)
    ON DELETE Cascade
    ON UPDATE Cascade
;

ALTER TABLE public.tag
    ADD CONSTRAINT "fk_tag_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE No Action
    ON UPDATE No Action
;

ALTER TABLE public.user_properties
    ADD CONSTRAINT "fk_user_properties_users"
    FOREIGN KEY (user_id)
    REFERENCES public.users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
;

/* Create Table Comments, Sequences for Autonumber Columns */

CREATE SEQUENCE IF NOT EXISTS public.users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.category_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.note_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.tag_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;


CREATE SEQUENCE IF NOT EXISTS public.user_properties_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;

CREATE SEQUENCE IF NOT EXISTS public.note_tag_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE
;