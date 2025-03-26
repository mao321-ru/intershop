package org.example.intershop.repository;

import org.example.intershop.model.Order;
import org.example.intershop.model.OrderProduct;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query(
        "select op.*, p.product_name, p.image_id from order_products op join products p on p.product_id = op.product_id" +
        " where op.order_id = :orderId order by p.product_name"
    )
    Flux<OrderProduct> findOrderProductByOrderId( Long orderId);

}
