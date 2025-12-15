package com.ecommerce.catalog.service;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface ProductService {
    Page<ProductResponseDTO> getAllProducts(@NonNull Pageable pageable);

    Page<ProductResponseDTO> searchProducts(String keyword, @NonNull Pageable pageable);

    Page<ProductResponseDTO> getProductsByCategory(Long categoryId, @NonNull Pageable pageable);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void deleteProduct(Long id);
}
