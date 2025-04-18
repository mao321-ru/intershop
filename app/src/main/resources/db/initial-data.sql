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
    -- у пользователей тривиальные пароли, совпадающие с логином (хэш BCryptPasswordEncoder::encode( username))
    select
        'admin' as login,
        '$2a$10$6JWkxBFepOhft3SJWuJET.gRTFnl/d9WwE8u8e8O8nB3JobvgHVCq' as password_hash,
        1 as admin_flag
    union all select 'user', '$2a$10$fkvOpBEpZEUj5qA/xWsSSeVFwjnBBKOqUIGTMmquMtD9rsLAu5DN2', 0
    union all select 'user2', '$2a$10$ECqpH8ofDrR6lWxiA0erq.O1dWQYrX8OI3/vGJjamcFOh7JBT98lG', 0
    union all select 'user3', '$2a$10$VguOCb6sV7JuMMh8YpGAeuBDq3dEHfPCuVilxSssLtYP7jrxCIg5a', 0
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
