create table if not exists cart_products(
    cart_product_id bigserial primary key,
    quantity integer not null check( quantity > 0),
    product_id bigint unique references products,
    create_time timestamp with time zone default current_timestamp not null
);
