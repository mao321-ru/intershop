package org.example.intershop.repository;

import org.example.intershop.model.Order;
import org.example.intershop.model.OrderProduct;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query(
        "select " +
            "o.* " +
        "from " +
            "orders o " +
            "join users u " +
                "on u.user_id = o.user_id " +
        "where " +
            "o.order_id = :orderId " +
            "and u.login = :login"
    )
    Mono<Order> findByIdAndLogin( Long orderId, String login);

    @Query(
        "select " +
            "o.* " +
        "from " +
            "users u " +
            "join orders o " +
                "on o.user_id = u.user_id " +
        "where " +
            "u.login = :login"
    )
    Flux<Order> findByLogin( String login);

    @Query(
        "select op.*, p.product_name, p.image_id from order_products op join products p on p.product_id = op.product_id" +
        " where op.order_id = :orderId order by p.product_name"
    )
    Flux<OrderProduct> findOrderProductByOrderId( Long orderId);

}
