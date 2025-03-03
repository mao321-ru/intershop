-- Картинки (товаров)
create table if not exists images(
    image_id bigserial primary key,
    orig_filename varchar(256) not null check( trim( orig_filename) != ''),
    content_type varchar(256) not null check( trim( content_type) != ''),
    file_data bytea not null,
    create_time timestamp with time zone default current_timestamp not null
);

-- Товары
create table if not exists products(
    product_id bigserial primary key,
    product_name varchar(256) not null check( trim( product_name) != ''),
    price numeric(38,2),
    description varchar(1000),
    image_id bigint unique references images,
    create_time timestamp with time zone default current_timestamp not null
);

-- Товары в корзине
create table if not exists cart_products(
    cart_product_id bigserial primary key,
    quantity integer not null check( quantity > 0),
    product_id bigint unique references products,
    create_time timestamp with time zone default current_timestamp not null
);
