alter table
    orders
add column
    order_total numeric(38,2)
;

update
    orders d
set
    order_total =
        (
        select
            sum( op.amount)
        from
            order_products op
        where
            op.order_id = d.order_id
        )
where
    d.order_total is null
;

alter table orders alter column order_total set not null;
alter table orders add constraint orders_order_total_check check( order_total >= 0);
