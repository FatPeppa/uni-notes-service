
/* Creating Tables */

CREATE TABLE IF NOT EXISTS public.users (
    id int8 NOT NULL,
    username varchar(30) NOT NULL,
    "password" varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    "role" varchar(40) NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE public.t_deactivated_token (
    id uuid NOT NULL,
    "token" varchar NOT NULL,
    created_date timestamp NOT NULL,
    CONSTRAINT t_deactivated_token_pk PRIMARY KEY (id)
);

/* Creating Indexes */

CREATE UNIQUE INDEX IF NOT EXISTS users_username_idx
    ON public.users
    USING btree (username);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_idx
    ON public.users
    USING btree (email);

/* Creating Sequences */

CREATE SEQUENCE public.users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;


