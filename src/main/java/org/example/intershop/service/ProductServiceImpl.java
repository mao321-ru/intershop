package org.example.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.dto.SliceProductDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Image;
import org.example.intershop.model.Product;
import org.example.intershop.repository.CartProductRepository;
import org.example.intershop.repository.ImageRepository;
import org.example.intershop.repository.ProductRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final R2dbcEntityTemplate etm;

    private final ProductRepository repo;
    private final ImageRepository imageRepo;
    private final CartProductRepository cartRepo;

    @Override
    @Transactional( readOnly = true)
    public Flux<ProductDto> findProducts(Sort sort) {
        var products = repo.findAll( sort);
        return products.map( ProductMapper::toProductDto);
    }

    @Override
    @Cacheable( value = "products", key = "{ #search, #pg.getPageSize(), #pg.getOffset(), #pg.getSort() }")
    @Transactional( readOnly = true)
    public Mono<SliceProductDto> findProducts(String search, Pageable pg) {
        final String likeStr = search != null && ! search.isEmpty() ? "%" + search + "%" : "%";
        final String orderBy = pg.getSort().isUnsorted()
                ? null
                : pg.getSort().stream()
                    .map( ord -> "p." + ord.getProperty() + ( ord.getDirection() == Sort.Direction.DESC ? " desc" : ""))
                    .collect( Collectors.joining( ", "));
        return
            etm.getDatabaseClient()
            .sql(
                """
                select
                    p.*,
                    cp.quantity as in_cart_quantity
                from
                    products p
                    left join cart_products cp
                        on cp.product_id = p.product_id
                where
                    upper( p.product_name) like upper( :namePat)
                    or upper( p.description) like upper( :descPat)
                $(order)
                limit $(limitCnt)
                $(offset)
                """
                .replace( "$(order)", orderBy != null ? "order by " + orderBy : "")
                .replace( "$(limitCnt)", String.valueOf( pg.getPageSize() + 1))
                .replace( "$(offset)", pg.getOffset() > 0 ? "offset " + pg.getOffset() : "")
            )
            .bind( "namePat", likeStr)
            .bind( "descPat", likeStr)
            .map(( row, metadata) -> Product.builder()
                .id( row.get( "product_id", Long.class))
                .name( row.get( "product_name", String.class))
                .price( row.get( "price", BigDecimal.class))
                .description( row.get( "description", String.class))
                .imageId( row.get( "image_id", Long.class))
                .inCartQuantity( row.get( "in_cart_quantity", Integer.class))
                .build()
            )
            .all()
            .collectList()
            .map( lst ->
                SliceProductDto.builder()
                    .content( lst.stream().limit( pg.getPageSize()).map( ProductMapper::toProductDto).toList())
                    .size( pg.getPageSize())
                    .number( pg.getPageNumber())
                    .isNext( lst.size() > pg.getPageSize())
                    .build()
            );
    }

    @Override
    @Cacheable( value = "product", key = "#productId")
    @Transactional( readOnly = true)
    public Mono<ProductDto> getProduct(Long productId) {
        return repo.findById( productId)
                .map( ProductMapper::toProductDto);
    }

    @Override
    @Cacheable( value = "image", key = "#productId")
    @Transactional( readOnly = true)
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
                    log.trace( "saveImage: fileData length: {}", fileData.length);
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
        log.debug( "createProduct service: dto file: {}", dto.getFile());
        return saveImage( null, dto.getFile())
            .flatMap( img -> {
                var pr = ProductMapper.toProduct( dto);
                pr.setImageId( img.getId());
                log.trace("pr: image_id: {}", pr.getImageId());
                return repo.save( pr);
            })
            .map( ProductMapper::toProductDto);
    }

    @Override
    @Transactional
    public Mono<Boolean> updateProduct( ProductUpdateDto dto) {
        final boolean delImage = dto.getDelImage() != null && dto.getDelImage();
        log.debug( "updateProduct: productId= {}", dto.getProductId());
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
                    return repo.setProduct(
                            pr.getName(),
                            pr.getPrice(),
                            pr.getDescription(),
                            pr.getImageId(),
                            pr.getId()
                        )
                        .then( delImageId != null ? imageRepo.deleteById( delImageId) : Mono.empty())
                        .thenReturn( Boolean.TRUE);
                })
                .defaultIfEmpty( Boolean.FALSE);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteProduct(Long productId) {
        log.debug( "deleteProduct: productId= {}", productId);
        return repo.findById( productId)
            .flatMap( pr ->  repo.deleteById( productId).thenReturn( pr))
            .flatMap( pr ->  pr.getImageId() != null
                    ? imageRepo.deleteById( pr.getImageId()).thenReturn( Boolean.TRUE)
                    : Mono.just( Boolean.TRUE)
            )
            .defaultIfEmpty( Boolean.FALSE);
    }

    @Override
    @Caching( evict = {
        @CacheEvict( value = "product", key = "#productId"),
        @CacheEvict( value = "products", allEntries = true),
        @CacheEvict( value = "cart", allEntries = true)
    })
    @Transactional
    public Mono<Void> changeInCartQuantity( Long productId, Integer delta) {
        return repo.findById( productId)
                .flatMap( pr -> {
                    log.debug( "inCartQuantity: {}", pr.getInCartQuantity());
                    Integer oldQty = pr.getInCartQuantity();
                    // Число товаров, которое должно быть после изменения
                    int qty = delta == null ? 0 : ( oldQty != null ? oldQty : 0) + delta;
                    Mono<Void> res = null;
                    if( qty > 0) {
                        if( oldQty == null) {
                            var cp = CartProduct.builder()
                                    .productId( pr.getId())
                                    .quantity( qty)
                                    .build();
                            res = cartRepo.save( cp).then();
                        }
                        else {
                            res = cartRepo.setQuantity( pr.getId(), qty);
                        }
                    }
                    else if ( oldQty != null) {
                        res = cartRepo.deleteByProductId( pr.getId());
                    }
                    return res;
                })
                .then();
    }

}
