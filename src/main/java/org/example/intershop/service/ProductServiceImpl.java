package org.example.intershop.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.intershop.dto.ProductCreateDto;
import org.example.intershop.dto.ProductDto;
import org.example.intershop.dto.ProductUpdateDto;
import org.example.intershop.mapper.ProductMapper;
import org.example.intershop.model.CartProduct;
import org.example.intershop.model.Product;
import org.example.intershop.model.Image;
import org.example.intershop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final EntityManager em;
    private final ProductRepository repo;

    @Override
    public Slice<ProductDto> findProducts(String search, Pageable pageable) {
        Slice<Product> products = repo.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
        return products.map( ProductMapper::toProductDto);
    }

    @Override
    @Transactional
    public void createProduct(ProductCreateDto dto) {
        repo.save( ProductMapper.toProduct( dto));
    }

    @Override
    public Optional<Image> findProductImage(long productId) {
        return repo.findEntityGraphTypeFetchById( productId).map( Product::getImage);
    }

    @Override
    @Transactional
    public void updateProduct(ProductUpdateDto pd) {
        Product pr = repo.findById( pd.getProductId()).orElseThrow();
        ProductMapper.changeProduct( pr, pd);
        repo.save( pr);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        repo.deleteById( productId);
    }

    @Override
    @Transactional
    public void changeInCartQuantity(long productId, int delta) {
        log.trace( "changeInCartQuantity: " + productId + ", delta: " + delta);
        Product pr = repo.findById( productId).orElseThrow();
        var cp = pr.getCartProduct();
        int qty = ( cp != null ? cp.getQuantity() : 0) + delta;
        if( qty > 0) {
            boolean isCreate = cp == null;
            if( isCreate) {
                cp = new CartProduct();
                cp.setProduct( pr);

            }
            cp.setQuantity( qty);
            em.persist( cp);
            log.trace( "cartProduct: " + cp.getId() + ": set qty: " + qty);
            if ( isCreate) {
                pr.setCartProduct( cp);
                repo.save( pr);
                log.trace( "save product: " + pr.getId());
            }
        }
        else if ( cp != null) {
            log.trace( "remove cartProduct: " + cp.getId());
            pr.setCartProduct( null);
            repo.save( pr);
            em.remove( cp);
        }
    }
}
