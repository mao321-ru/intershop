-- Актуальная схема БД

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
    price numeric(38,2) not null check( price >= 0),
    description varchar(1000),
    image_id bigint unique references images,
    create_time timestamp with time zone default current_timestamp not null
);

-- Товары в корзине
create table if not exists cart_products(
    cart_product_id bigserial primary key,
    quantity integer not null check( quantity > 0),
    product_id bigint not null unique references products,
    create_time timestamp with time zone default current_timestamp not null
);

-- Заказы
create table if not exists orders(
    order_id bigserial primary key,
    order_number bigserial not null,
    order_total numeric(38,2) not null check( order_total >= 0),
    create_time timestamp with time zone default current_timestamp not null
);

-- Товары в заказе
create table if not exists order_products(
    order_product_id bigserial primary key,
    order_id bigint not null references orders,
    product_id bigint not null references products,
    quantity integer not null check( quantity > 0),
    amount numeric(38,2) not null check( amount >= 0),
    create_time timestamp with time zone default current_timestamp not null,
    constraint order_products_uk unique ( order_id, product_id)
);

-- индекс для FK
create index if not exists
    order_products_ix_product_id
on
    order_products( product_id)
;
