-- Товары
create table if not exists products(
    product_id bigserial primary key,
    product_name varchar(256) not null check( trim( product_name) != ''),
    price numeric(38,2),
    create_time timestamp with time zone default current_timestamp not null
);

-- Картинки товаров
create table if not exists product_images(
    product_id bigint not null unique references products,
    orig_filename varchar(256) not null check( trim( orig_filename) != ''),
    content_type varchar(256) not null check( trim( content_type) != ''),
    file_data bytea not null,
    create_time timestamp with time zone default current_timestamp not null
);
