package org.example.intershop.service;

import com.example.payclient.api.PaymentApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.BitSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final R2dbcEntityTemplate etm;

    private final ProductRepository productRepo;

    private final PaymentApi paySrv;

    @Override
    @Cacheable( value = "cart")
    @Transactional( readOnly = true)
    public Mono<CartProducts> findCartProducts() {
        return productRepo.findInCart()
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
        return cartProducts
            .flatMap( cp -> cp.products().isEmpty()
                // в случае пустой корзины не обращаемся к платежному сервису
                ? Mono.just( new Cart( cp.products(), cp.total(), true, ""))
                // проверка баланса для оплаты корзины
                : paySrv.getBalance()
                    .map( bl -> {
                        boolean enabled = cp.total().compareTo( BigDecimal.valueOf( bl.getAmount())) >= 0;
                        return
                            new Cart(
                                cp.products(),
                                cp.total(),
                                enabled,
                                enabled ? "" : "Недостаточно средств"
                            );
                    })
                    .onErrorResume( e -> {
                        log.debug( "Error on payment service: " + e.getMessage());
                        return Mono.just(
                            new Cart(
                                cp.products(),
                                cp.total(),
                                false,
                                "Платежный сервис: " + e.getMessage()
                            )
                        );
                    })
            );
    }

    @Override
    @Caching( evict = {
            @CacheEvict( value = "product", allEntries = true),
            @CacheEvict( value = "products", allEntries = true),
            @CacheEvict( value = "cart", allEntries = true)
    })
    @Transactional
    public Mono<Long> buy() {
        final DatabaseClient dc = etm.getDatabaseClient();
        return
            // создает заказ и возвращает его Id в случае наличия товаров в корзине
            dc.sql( "insert into orders ( order_total) select 0 from cart_products limit 1")
                .filter( s -> s.returnGeneratedValues("order_id"))
                .map( row -> row.get("order_id", Long.class))
                .one()
            .flatMap( orderId -> {
                    log.trace( "order inserted: orderId: {}", orderId);
                    // добавляем товары из корзины в заказ
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
                            :orderId as order_id,
                            cp.product_id,
                            cp.quantity,
                            cp.quantity * p.price as amount
                        from
                            cart_products cp
                            join products p
                                on p.product_id = cp.product_id
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
                            .fetch()
                            .rowsUpdated()
                    )
                    // удаляем из корзины товары, которые добавили в заказ
                    .then( dc.sql("""
                        delete from
                            cart_products cp
                        where
                            cp.product_id in
                                (
                                select
                                    op.product_id
                                from
                                    order_products op
                                where
                                    op.order_id = :orderId
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
