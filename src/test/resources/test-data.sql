delete from cart_products;
delete from products;
delete from images;

alter sequence images_image_id_seq restart with 1;
alter sequence products_product_id_seq restart with 1;
alter sequence cart_products_cart_product_id_seq restart with 1;


insert into
    images
(
    orig_filename,
    content_type,
    file_data
)
select
    s.*
from
    (
    select
        'red_pixel.png' as orig_filename,
        'image/png' as content_type,
        '\x89504E470D0A1A0A0000000D4948445200000001000000010802000000907753DE0000000C4944415408D763F8CFC000000301010018DD8DB0000000000049454E44AE426082'::bytea
            as file_data
    ) s
where
    s.orig_filename not in
        (
        select
            t.orig_filename
        from
            images t
        )
;

insert into
    products
(
    product_name,
    price,
    description,
    image_id
)
select
    s.*
from
    (
    select 'Шампунь SUPER' as product_name, 10.81 as price, 'Шампунь с приятным запахом', 1 as image_id
    union all select 'Мыло DURU', 5.00, 'Качественное недорогое мыло',  null
    union all select 'Супер-пена (findProducts_byNameOrDesc_seaRch)', 16.00, 'Пена для ванны с запахом весеннего леса',  null
    union all select 'Пена для ванны Forest', 15.00, 'Пена findProducts_byNameOrDesc_search с запахом леса',  null
    ) s
where
    s.product_name not in
        (
        select
            t.product_name
        from
            products t
        )
;


-- id для временных данных (создавемые в процессе тестов) начинаются с 1001
alter sequence images_image_id_seq restart with 1001;
alter sequence products_product_id_seq restart with 1001;
alter sequence cart_products_cart_product_id_seq restart with 1001;
