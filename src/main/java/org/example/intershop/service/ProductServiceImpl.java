package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.mapper.ProductMapper;
//import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Image;
import org.example.intershop.repository.ImageRepository;
import org.example.intershop.repository.ProductRepository;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;


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
    public Mono<Image> findProductImage( long productId) {
        return imageRepo.findByProductId( productId);
    }

    // В случае передачи пустого FilePart сохранение не выполняет и возвращает Image с указанным imageId
    private Mono<Image> saveImage( Long imageId, FilePart f) {
        return Mono.defer( () ->
                f == null || f.filename() == null || f.filename().isEmpty()
                        ? Mono.empty()
                        : DataBufferUtils.join( f.content())
            )
            .flatMap( buf -> {
                try {
                    byte[] fileData = buf.asInputStream().readAllBytes();
                    log.trace( "saveImage: fileData length: " + fileData.length);
                    var img = Image.builder()
                        .id( imageId)
                        .origFilename( f.filename())
                        .contentType( f.headers().getContentType().toString())
                        .fileData( fileData)
                        .build();
                    return imageRepo.save( img);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    // возможно явно освобождать не нужно...
                    DataBufferUtils.release( buf);
                }
            })
            .defaultIfEmpty( Image.builder().id( imageId).build());
    }

    @Override
    @Transactional
    public Mono<ProductDto> createProduct(ProductCreateDto dto) {
        log.debug( "createProduct service: dto file: " + dto.getFile());
        return saveImage( null, dto.getFile())
            .flatMap( img -> {
                var pr = ProductMapper.toProduct( dto);
                pr.setImageId( img.getId());
                log.trace("pr: image_id: " + pr.getImageId());
                return repo.save( pr);
            })
            .map( ProductMapper::toProductDto);
    }

    @Override
    @Transactional
    public Mono<Boolean> updateProduct( ProductUpdateDto dto) {
        final boolean delImage = dto.getDelImage() != null && dto.getDelImage();
        log.debug( "updateProduct: productId= " + dto.getProductId());
        return repo.findById( dto.getProductId())
                .flatMap( pr -> ! delImage
                    ? saveImage( pr.getImageId(), dto.getFile())
                        .map( img ->  {
                            pr.setImageId( img.getId());
                            return pr;
                        })
                    : Mono.just( pr)
                )
                .flatMap( pr -> {
                    ProductMapper.changeProduct(pr, dto);
                    Long delImageId = delImage ? pr.getImageId() : null;
                    if ( delImageId != null) pr.setImageId( null);
                    return repo.save( pr)
                        .then( delImageId != null ? imageRepo.deleteById( delImageId) : Mono.empty())
                        .thenReturn( Boolean.TRUE);
                })
                .defaultIfEmpty( Boolean.FALSE);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteProduct(Long productId) {
        log.debug( "deleteProduct: productId= " + productId);
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
