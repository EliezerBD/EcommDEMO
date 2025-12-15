package com.ecommerce.catalog.service;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.mapper.ProductMapper;
import com.ecommerce.catalog.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(@NonNull Pageable pageable) {
        log.debug("Fetching all products - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDTO> result = productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);
        log.info("Retrieved {} products", result.getTotalElements());
        return result;
    }

    @Override
    public Page<ProductResponseDTO> searchProducts(String keyword, @NonNull Pageable pageable) {
        log.debug("Searching products with keyword: '{}'", keyword);
        Page<ProductResponseDTO> result = productRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword, pageable)
                .map(productMapper::toResponseDTO);
        log.info("Search for '{}' returned {} products", keyword, result.getTotalElements());
        return result;
    }

    @Override
    public Page<ProductResponseDTO> getProductsByCategory(Long categoryId, @NonNull Pageable pageable) {
        log.debug("Fetching products by category ID: {}", categoryId);
        Page<ProductResponseDTO> result = productRepository.findByCategoryId(categoryId, pageable)
                .map(productMapper::toResponseDTO);
        log.info("Retrieved {} products for category ID: {}", result.getTotalElements(), categoryId);
        return result;
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });
        log.debug("Product found: {}", product.getName());
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.info("Creating product: {}", productRequestDTO.getName());
        Product product = productMapper.toEntity(productRequestDTO);
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to update non-existent product with ID: {}", id);
                    return new ResourceNotFoundException("Producto no encontrado con id: " + id);
                });

        productMapper.updateEntityFromDTO(productRequestDTO, product);

        Product updatedProduct = productRepository.save(product);
        log.info("Product {} updated successfully", id);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent product with ID: {}", id);
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product {} deleted successfully", id);
    }
}