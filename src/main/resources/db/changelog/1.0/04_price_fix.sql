alter table products alter column price set not null;
alter table products add constraint products_price_check check( price >= 0);
