create table if not exists orders(
    order_id bigserial primary key,
    order_number bigserial not null,
    create_time timestamp with time zone default current_timestamp not null
);

create table if not exists order_products(
    order_product_id bigserial primary key,
    order_id bigint not null references orders,
    product_id bigint not null references products,
    quantity integer not null check( quantity > 0),
    amount numeric(38,2) not null check( amount >= 0),
    create_time timestamp with time zone default current_timestamp not null,
    constraint order_products_uk unique ( order_id, product_id)
);

create index if not exists
    order_products_ix_product_id
on
    order_products( product_id)
;
