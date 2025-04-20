package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

    // перекрыт стандартный поиск по Id для заполнения вычисляемых колонок
    @Query(
        "select " +
            "p.*, " +
            "cp.quantity as in_cart_quantity " +
        "from " +
            "( " +
            "select " +
                "pr.*, " +
                "(select u.user_id from users u where u.login = :login) as user_id " +
            "from " +
                "products pr " +
            "where " +
                "pr.product_id = :productId " +
            ") p " +
            "left join cart_products cp " +
                "on cp.user_id = p.user_id " +
                    "and cp.product_id = p.product_id " +
        "limit 2"
    )
    Mono<Product> findByIdForLogin( Long productId, String login);

    // продукты в корзине
    @Query(
        "select " +
            "p.*, cp.quantity as in_cart_quantity " +
        "from " +
            "users u " +
            "join cart_products cp on cp.user_id = u.user_id " +
            "join products p on p.product_id = cp.product_id " +
        "where " +
            "u.login = :login " +
        "order by " +
            "p.product_name"
    )
    Flux<Product> findInCartByLogin( String login);

    // исключаем из обновления вычисляемое поле inCartQuantity
    @Modifying
    @Query("UPDATE products SET product_name = :productName, price = :price, description = :description, image_id = :imageId WHERE products.product_id = :productId")
    Mono<Void> setProduct(String productName, BigDecimal price, String description, Long imageId, Long productId);
}
