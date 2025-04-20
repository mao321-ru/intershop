-- Добавление колонки user_id в таблицы cart_products и orders

alter table
    cart_products
add column
    user_id bigint references users
;

update
    cart_products
set
    user_id = ( select u.user_id from users u where u.login = 'user')
where
    user_id is null
;

alter table cart_products alter column user_id set not null;

alter table cart_products drop constraint cart_products_product_id_key;

alter table cart_products add constraint cart_products_uk unique ( user_id, product_id);

create index if not exists
    cart_products_ix_product_id
on
    cart_products( product_id)
;



alter table
    orders
add column
    user_id bigint references users
;

update
    orders
set
    user_id = ( select u.user_id from users u where u.login = 'user')
where
    user_id is null
;

alter table orders alter column user_id set not null;

-- индекс для FK
create index if not exists
    orders_ix_user_id
on
    orders( user_id)
;
