package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.Product;
import org.example.intershop.model.ProductImage;
import org.example.intershop.repository.ProductImageRepository;
import org.example.intershop.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final ProductImageRepository imgRepo;

    @Override
    public Page<ProductDto> findProducts(Pageable pageable) {
        Page<Product> products = repo.findAll( pageable);
        return products.map( ProductMapper::toProductDto);
    }

    @Override
    public void createProduct(ProductCreateDto dto) {
        var pr = ProductMapper.toProduct( dto);
        repo.save( pr);
        if( ! pr.getImages().isEmpty()) {
            var img = pr.getImages().getFirst();
            img.setProductId( pr.getProductId());
            imgRepo.save( img);
        }
    }

    @Override
    public Optional<ProductImage> findProductImage(long productId) {
        return Optional.ofNullable( imgRepo.findByProductId( productId));
    }
}
