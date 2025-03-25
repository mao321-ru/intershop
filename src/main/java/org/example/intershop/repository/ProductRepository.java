package org.example.intershop.repository;

import org.example.intershop.model.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ProductRepository extends R2dbcRepository<Product, Long> {

    // перекрыт стандартный поиск по Id для заполняения вычисляемой колонки
    @Query( "SELECT p.*, (select cp.quantity from cart_products cp where cp.product_id = p.product_id) as in_cart_quantity FROM products p WHERE p.product_id = :product_id LIMIT 2")
    Mono<Product> findById( Long productId);

    // продукты в корзине
    @Query( "select p.*, cp.quantity as in_cart_quantity from products p join cart_products cp on cp.product_id = p.product_id order by p.product_name")
    Flux<Product> findInCart();

    // исключаем из обновления вычисляемое поле inCartQuantity
    @Modifying
    @Query("UPDATE products SET product_name = :productName, price = :price, description = :description, image_id = :imageId WHERE products.product_id = :productId")
    Mono<Void> setProduct(String productName, BigDecimal price, String description, Long imageId, Long productId);
}
