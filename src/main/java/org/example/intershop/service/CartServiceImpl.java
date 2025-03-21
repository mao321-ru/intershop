package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Order;
import org.example.intershop.model.OrderProduct;
import org.example.intershop.model.Product;
import org.example.intershop.repository.CartProductRepository;
import org.example.intershop.repository.OrderRepository;
import org.example.intershop.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartProductRepository repo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    @Override
    public CartInfo findCartProducts() {
        // сортировка для стабильности отображения
        List<ProductDto> products = repo.findAll( Sort.by( "product.name"))
                .stream().map(cp -> ProductMapper.toProductDto( cp.getProduct())).toList();
        BigDecimal total = products.stream().map(
                ( p) -> p.getPrice().multiply( BigDecimal.valueOf( p.getInCartQuantity()))
            ).reduce( BigDecimal.ZERO, BigDecimal::add);
        return new CartInfo( products, total);
    }

    @Override
    @Transactional
    public long buy() {
        List<CartProduct> cartProducts = repo.findAll();
        if( cartProducts.isEmpty()) throw new NoSuchElementException();
        Order order = new Order();
        order.setProducts(
            cartProducts.stream()
                .map( p ->
                    OrderProduct.builder()
                        .quantity( p.getQuantity())
                        .amount( p.getProduct().getPrice().multiply( BigDecimal.valueOf( p.getQuantity())))
                        .order( order)
                        .product( p.getProduct())
                        .build()
                ).toList()
        );
        order.setTotal(
            order.getProducts().stream()
                .map( OrderProduct::getAmount)
                .reduce( BigDecimal.ZERO, BigDecimal::add)
        );
        orderRepo.save( order);
        // очистка корзины
        cartProducts.stream().forEach( cp -> {
            var p = cp.getProduct();
            p.setCartProduct( null);
            productRepo.save( p);
        });
        return order.getId();
    }

}
