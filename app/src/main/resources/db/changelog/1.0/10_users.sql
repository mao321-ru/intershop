-- Пользователи
create table if not exists users(
    user_id bigserial primary key,
    login varchar(128) not null check( trim( login) != '') unique,
    password_hash varchar(128) not null,
    admin_flag numeric(1) default 0 not null check( admin_flag in (0, 1)),
    create_time timestamp with time zone default current_timestamp not null
);
