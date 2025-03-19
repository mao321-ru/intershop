package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
//import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.mapper.ProductMapper;
//import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Image;
import org.example.intershop.repository.ImageRepository;
import org.example.intershop.repository.ProductRepository;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final ImageRepository imageRepo;

    @Override
    public Flux<ProductDto> findProducts(Sort sort) {
        var products = repo.findAll( sort);
        return products.map( ProductMapper::toProductDto);
    }

//    @Override
//    public Slice<ProductDto> findProducts(String search, Pageable pageable) {
//        Slice<Product> products = repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
//        return products.map( ProductMapper::toProductDto);
//    }

    @Override
    public Mono<ProductDto> getProduct(Long productId) {
        return repo.findById( productId)
                .map( ProductMapper::toProductDto);
    }

    @Override
    @Transactional
    public Mono<ProductDto> createProduct(ProductCreateDto dto) {
        log.debug( "createProduct service: dto file: " + dto.getFile());
        return
            Mono.defer( () ->
                dto.getFile().filename().isEmpty()
                    ? Mono.empty()
                    : DataBufferUtils.join( dto.getFile().content())
            )
            .flatMap( buf -> {
                try {
                    byte[] fileData = buf.asInputStream().readAllBytes();
                    log.trace( "fileData: length: " + fileData.length);
                    var img = ProductMapper.toProduct( dto).getImage();
                    img.setFileData( fileData);
                    return imageRepo.save( img);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    // возможно явно освобождать не нужно...
                    DataBufferUtils.release( buf);
                }
            })
            .defaultIfEmpty( new Image())
            .flatMap( img -> {
                var pr = ProductMapper.toProduct( dto);
                log.trace("pr: image_id: " + img.getId());
                pr.setImageId( img.getId());
                return repo.save( pr);
            })
            .map( ProductMapper::toProductDto);
    }

    @Override
    public Mono<Image> findProductImage( long productId) {
        return imageRepo.findByProductId( productId);
    }

//    @Override
//    @Transactional
//    public void updateProduct(ProductUpdateDto pd) {
//        Product pr = repo.findById( pd.getProductId()).orElseThrow();
//        ProductMapper.changeProduct( pr, pd);
//        repo.save( pr);
//    }

    @Override
    @Transactional
    public Mono<Boolean> deleteProduct(Long productId) {
        return repo.findById( productId)
            .flatMap( pr ->  repo.deleteById( productId).thenReturn( pr))
            .flatMap( pr ->  pr.getImageId() != null
                    ? imageRepo.deleteById( pr.getImageId()).thenReturn( Boolean.TRUE)
                    : Mono.just( Boolean.TRUE)
            )
            .defaultIfEmpty( Boolean.FALSE);
    }

//    @Override
//    @Transactional
//    public void changeInCartQuantity(long productId, Integer delta) {
//        Product pr = repo.findById( productId).orElseThrow();
//        CartProduct cp = pr.getCartProduct();
//        // Число товаров, которое должно быть после изменения
//        int qty = delta == null ? 0 : ( cp != null ? cp.getQuantity() : 0) + delta;
//        if( qty > 0) {
//            if( cp == null) {
//                cp = new CartProduct();
//                pr.setCartProduct( cp);
//                cp.setProduct( pr);
//            }
//            cp.setQuantity( qty);
//        }
//        else if ( cp != null) {
//            pr.setCartProduct( null);
//        }
//        repo.save( pr);
//    }
}
