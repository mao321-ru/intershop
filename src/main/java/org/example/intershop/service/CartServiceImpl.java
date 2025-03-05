package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.Product;
import org.example.intershop.repository.CartProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartProductRepository repo;

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

}
