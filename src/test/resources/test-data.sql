insert into
    products
(
    product_name,
    price
)
select
    s.*
from
    (
    select 'Шампунь SUPER' as product_name, 10.81 as price
    union all select 'Мыло DURU', 5.00
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
