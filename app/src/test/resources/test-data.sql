delete from order_products;
delete from orders;
delete from cart_products;
delete from products;
delete from images;
delete from users;

alter sequence users_user_id_seq restart with 1;
alter sequence images_image_id_seq restart with 1;
alter sequence products_product_id_seq restart with 1;
alter sequence cart_products_cart_product_id_seq restart with 1;
alter sequence orders_order_id_seq restart with 1;
alter sequence orders_order_number_seq restart with 1;
alter sequence order_products_order_product_id_seq restart with 1;

insert into
    users
(
    login,
    password_hash,
    admin_flag
)
select
    s.*
from
    (
    select 'admin' as login, '-' as password_hash, 1 as admin_flag
    union all select 'user', '-', 0
    ) s
;

insert into
    images
(
    orig_filename,
    content_type,
    file_data
)
select
    'red_pixel.png' as orig_filename,
    'image/png' as content_type,
    '\x89504E470D0A1A0A0000000D4948445200000001000000010802000000907753DE0000000C4944415408D763F8CFC000000301010018DD8DB0000000000049454E44AE426082'::bytea
        as file_data
union all select
    'bad.jpg',
    'image/jpg',
    '\x89504E'::bytea
;

insert into
    products
(
    product_name,
    price,
    description,
    image_id
)
select 'Шампунь SUPER' as product_name, 10.81 as price, 'Шампунь с приятным запахом', 1 as image_id
union all select 'Мыло DURU', 5.00, 'Качественное недорогое мыло', null
union all select 'Супер-пена (findProducts_byNameOrDesc_seaRch)', 16.00, 'Пена для ванны с запахом весеннего леса', null
union all select 'Пена для ванны Forest', 15.00, 'Пена findProducts_byNameOrDesc_search с запахом леса', null
union all select 'Непродававшийся (для удаления)', 18.00, 'Для тестирования удаления в deleteProduct_check', 2
;

insert into
    orders
(
    order_total
)
values
(
    25.02
);

insert into
    order_products
(
    order_id,
    product_id,
    quantity,
    amount
)
select 1 as order_id, 1 as product_id, 2 as quantity, 20.02 as amount
union all select 1, 2, 1, 5.00
;


-- id для временных данных (создавемые в процессе тестов) начинаются с 1001
alter sequence users_user_id_seq restart with 1001;
alter sequence images_image_id_seq restart with 1001;
alter sequence products_product_id_seq restart with 1001;
alter sequence cart_products_cart_product_id_seq restart with 1001;
alter sequence orders_order_id_seq restart with 1001;
alter sequence orders_order_number_seq restart with 1001;
alter sequence order_products_order_product_id_seq restart with 1001;
