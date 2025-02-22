-- Товары
create table if not exists products(
    product_id bigserial primary key,
    product_name varchar(256) not null check( trim( product_name) != ''),
    price numeric(38,2),
    create_time timestamp with time zone default current_timestamp not null
);
