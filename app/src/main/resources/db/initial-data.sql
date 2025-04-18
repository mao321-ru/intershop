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
where
    s.login not in
        (
        select
            t.login
        from
            users t
        )
;
