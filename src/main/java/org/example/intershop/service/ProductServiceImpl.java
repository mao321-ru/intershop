package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.Product;
import org.example.intershop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    @Override
    public Page<ProductDto> findProducts(Pageable pageable) {
        Page<Product> products = repo.findAll( pageable);
        return products.map( ProductMapper::toProductDto);
    }
}
