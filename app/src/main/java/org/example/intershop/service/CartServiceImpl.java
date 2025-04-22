package org.example.intershop.service;

import com.example.payclient.api.PaymentApi;
import com.example.payclient.domain.Purchase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final R2dbcEntityTemplate etm;

    private final ProductRepository productRepo;

    private final PaymentApi paySrv;

    @Override
    @Cacheable( value = "cart", key = "#userLogin")
    @Transactional( readOnly = true)
    public Mono<CartProducts> findCartProducts(String userLogin) {
        return
            productRepo.findInCartByLogin( userLogin)
            .map( ProductMapper::toProductDto)
            .collectList()
            .map( products -> {
                BigDecimal total = products.stream()
                    .map( p -> p.getPrice().multiply( BigDecimal.valueOf( p.getInCartQuantity())))
                    .reduce( BigDecimal.ZERO, BigDecimal::add);
                return new CartProducts( products, total);
            });
    }

    @Override
    public Mono<Cart> getCart(Mono<CartProducts> cartProducts) {
        return cartProducts.flatMap( cp -> {
            log.debug("cart total: {}", cp.total());
            return
                cp.products().isEmpty()
                    // в случае пустой корзины не обращаемся к платежному сервису
                    ? Mono.just(new Cart(cp.products(), cp.total(), true, ""))
                    // проверка баланса для оплаты корзины
                    : paySrv.getBalance()
                .map(bl -> {
                    BigDecimal balance = bl.getAmount();
                    log.debug("balance: {}", balance);
                    boolean enabled = cp.total().compareTo( balance) <= 0;
                    return
                            new Cart(
                                    cp.products(),
                                    cp.total(),
                                    enabled,
                                    enabled ? "" : "Недостаточно средств"
                            );
                })
                .onErrorResume(e -> {
                    log.debug("Error on payment service: " + e.getMessage());
                    return Mono.just(
                            new Cart(
                                    cp.products(),
                                    cp.total(),
                                    false,
                                    "Платежный сервис: " + e.getMessage()
                            )
                    );
                });
        });
    }

    @Override
    @Caching( evict = {
            @CacheEvict( value = "product", allEntries = true),
            @CacheEvict( value = "products", allEntries = true),
            @CacheEvict( value = "cart", key = "#userLogin")
    })
    @Transactional
    public Mono<Long> buy( String userLogin) {
        final DatabaseClient dc = etm.getDatabaseClient();
        return
            // создает заказ и возвращает его Id в случае наличия товаров в корзине
            dc.sql("""
                insert into
                    orders
                (
                    user_id,
                    order_total
                )
                select
                    u.user_id,
                    0 as order_total
                from
                    users u
                    join cart_products cp
                        on cp.user_id = u.user_id
                where
                    u.login = :login
                limit 1
                """)
                .bind( "login", userLogin)
                .filter( s -> s.returnGeneratedValues("order_id"))
                .map( row -> row.get("order_id", Long.class))
                .one()
            .flatMap( orderId -> {
                    log.trace( "order inserted: orderId: {}", orderId);
                    // добавляем товары из корзины пользователя в заказ
                    return dc.sql("""
                        insert into
                            order_products
                        (
                            order_id,
                            product_id,
                            quantity,
                            amount
                        )
                        select
                            o.order_id,
                            cp.product_id,
                            cp.quantity,
                            cp.quantity * p.price as amount
                        from
                            orders o
                            join cart_products cp
                                on cp.user_id = o.user_id
                            join products p
                                on p.product_id = cp.product_id
                        where
                            o.order_id = :orderId
                        """)
                            .bind( "orderId", orderId)
                            .fetch()
                            .rowsUpdated()
                            .doOnNext( n -> log.trace( "products in order: {}", n))
                    // подсчитываем и сохраняем сумму заказа
                    .then( dc.sql("""
                        update
                            orders o
                        set
                            order_total =
                                (
                                select
                                    sum( op.amount)
                                from
                                    order_products op
                                where
                                    op.order_id = o.order_id
                                )
                        where
                            o.order_id = :orderId
                        """)
                            .bind( "orderId", orderId)
                            .filter( s -> s.returnGeneratedValues("order_total"))
                            .map( row -> row.get("order_total", BigDecimal.class))
                            .one()
                            .doOnNext( total -> log.trace( "amount for pay: {}", total))
                    )
                    // списание платы через платежный сервис
                    .flatMap( total -> paySrv.pay( new Purchase().amount( total))
                        .onErrorMap( e -> new RuntimeException( "Error on pay request", e))
                    )
                    // удаляем из корзины товары, которые добавили в заказ
                    .then( dc.sql("""
                        delete from
                            cart_products cp
                        where
                            ( cp.user_id, cp.product_id) in
                                (
                                select
                                    o.user_id,
                                    op.product_id
                                from
                                    orders o
                                    join order_products op
                                        on op.order_id = o.order_id
                                where
                                    o.order_id = :orderId
                                )
                        """)
                            .bind( "orderId", orderId)
                            .fetch()
                            .rowsUpdated()
                            .doOnNext( n -> log.trace( "cart products deleted: {}", n))
                    )
                    .thenReturn( orderId)
                ;
            });
    }

}
