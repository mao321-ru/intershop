create table if not exists images(
    image_id bigserial primary key,
    orig_filename varchar(256) not null check( trim( orig_filename) != ''),
    content_type varchar(256) not null check( trim( content_type) != ''),
    file_data bytea not null,
    create_time timestamp with time zone default current_timestamp not null
);
