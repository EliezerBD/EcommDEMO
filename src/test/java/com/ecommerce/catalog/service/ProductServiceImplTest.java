package com.ecommerce.catalog.service;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.mapper.ProductMapper;
import com.ecommerce.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para ProductServiceImpl.
 * Utiliza Mockito para aislar la lógica de negocio del repositorio.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;
    private PageRequest pageable;

    @BeforeEach
    void setUp() {
        // Crear productos de prueba con todos los campos
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setDescription("Laptop Core i7");
        product1.setPrice(BigDecimal.valueOf(1200.0));
        product1.setStock(10);
        product1.setCategoryId(1L);
        product1.setSku("LAP-001");
        product1.setActive(true);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse Gaming");
        product2.setDescription("Mouse RGB 5 botones");
        product2.setPrice(BigDecimal.valueOf(50.0));
        product2.setStock(25);
        product2.setCategoryId(2L);
        product2.setSku("MOU-001");
        product2.setActive(true);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Laptop");
        productRequestDTO.setDescription("Laptop Core i7");
        productRequestDTO.setPrice(BigDecimal.valueOf(1200.0));
        productRequestDTO.setStock(10);
        productRequestDTO.setCategoryId(1L);
        productRequestDTO.setSku("LAP-001");
        productRequestDTO.setActive(true);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1L);
        productResponseDTO.setName("Laptop");
        productResponseDTO.setDescription("Laptop Core i7");
        productResponseDTO.setPrice(BigDecimal.valueOf(1200.0));
        productResponseDTO.setStock(10);
        productResponseDTO.setCategoryId(1L);
        productResponseDTO.setSku("LAP-001");
        productResponseDTO.setActive(true);

        pageable = PageRequest.of(0, 10);
    }

    // ========================================
    // Tests para getProductById
    // ========================================

    @Test
    void testGetProductByIdSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productMapper.toResponseDTO(product1)).thenReturn(productResponseDTO);

        ProductResponseDTO foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
        assertEquals(BigDecimal.valueOf(1200.0), foundProduct.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).toResponseDTO(product1);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productMapper, never()).toResponseDTO(any());
    }

    // ========================================
    // Tests para getAllProducts
    // ========================================

    @Test
    void testGetAllProducts() {
        Page<Product> page = new PageImpl<>(Arrays.asList(product1, product2));
        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toResponseDTO(product1)).thenReturn(productResponseDTO);
        when(productMapper.toResponseDTO(product2)).thenReturn(new ProductResponseDTO());

        Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
        verify(productMapper, times(2)).toResponseDTO(any(Product.class));
    }

    @Test
    void testGetAllProductsEmpty() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(productRepository, times(1)).findAll(pageable);
        verify(productMapper, never()).toResponseDTO(any());
    }

    // ========================================
    // Tests para searchProducts
    // ========================================

    @Test
    void testSearchProductsByName() {
        Page<Product> page = new PageImpl<>(Arrays.asList(product1));
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "Laptop", "Laptop", pageable)).thenReturn(page);
        when(productMapper.toResponseDTO(product1)).thenReturn(productResponseDTO);

        Page<ProductResponseDTO> result = productService.searchProducts("Laptop", pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent().get(0).getName());
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Laptop", "Laptop", pageable);
    }

    @Test
    void testSearchProductsByDescription() {
        Page<Product> page = new PageImpl<>(Arrays.asList(product2));
        ProductResponseDTO mouseDTO = new ProductResponseDTO();
        mouseDTO.setId(2L);
        mouseDTO.setName("Mouse Gaming");
        mouseDTO.setDescription("Mouse RGB 5 botones");
        mouseDTO.setPrice(BigDecimal.valueOf(50.0));

        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "RGB", "RGB", pageable)).thenReturn(page);
        when(productMapper.toResponseDTO(product2)).thenReturn(mouseDTO);

        Page<ProductResponseDTO> result = productService.searchProducts("RGB", pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getDescription().contains("RGB"));
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("RGB", "RGB", pageable);
    }

    @Test
    void testSearchProductsNoResults() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
        when(productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "NoExiste", "NoExiste", pageable)).thenReturn(emptyPage);

        Page<ProductResponseDTO> result = productService.searchProducts("NoExiste", pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(productRepository, times(1))
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("NoExiste", "NoExiste", pageable);
    }

    // ========================================
    // Tests para createProduct
    // ========================================

    @Test
    void testCreateProductSuccess() {
        when(productMapper.toEntity(productRequestDTO)).thenReturn(product1);
        when(productRepository.save(product1)).thenReturn(product1);
        when(productMapper.toResponseDTO(product1)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.createProduct(productRequestDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(1200.0), result.getPrice());
        verify(productMapper, times(1)).toEntity(productRequestDTO);
        verify(productRepository, times(1)).save(product1);
        verify(productMapper, times(1)).toResponseDTO(product1);
    }

    // ========================================
    // Tests para updateProduct
    // ========================================

    @Test
    void testUpdateProductSuccess() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Laptop Actualizada");
        updateDTO.setDescription("Laptop Core i9");
        updateDTO.setPrice(BigDecimal.valueOf(1500.0));
        updateDTO.setStock(15);
        updateDTO.setSku("LAP-001");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Laptop Actualizada");
        updatedProduct.setDescription("Laptop Core i9");
        updatedProduct.setPrice(BigDecimal.valueOf(1500.0));
        updatedProduct.setStock(15);
        updatedProduct.setSku("LAP-001");
        updatedProduct.setActive(true);

        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO();
        updatedResponseDTO.setId(1L);
        updatedResponseDTO.setName("Laptop Actualizada");
        updatedResponseDTO.setDescription("Laptop Core i9");
        updatedResponseDTO.setPrice(BigDecimal.valueOf(1500.0));
        updatedResponseDTO.setStock(15);
        updatedResponseDTO.setSku("LAP-001");
        updatedResponseDTO.setActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        doNothing().when(productMapper).updateEntityFromDTO(updateDTO, product1);
        when(productRepository.save(product1)).thenReturn(updatedProduct);
        when(productMapper.toResponseDTO(updatedProduct)).thenReturn(updatedResponseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Laptop Actualizada", result.getName());
        assertEquals(BigDecimal.valueOf(1500.0), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productMapper, times(1)).updateEntityFromDTO(updateDTO, product1);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void testUpdateProductNotFound() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Producto Inexistente");
        updateDTO.setDescription("Descripción");
        updateDTO.setPrice(BigDecimal.valueOf(100.0));

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(999L, updateDTO);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).findById(999L);
        verify(productMapper, never()).updateEntityFromDTO(any(), any());
        verify(productRepository, never()).save(any());
    }

    // ========================================
    // Tests para deleteProduct
    // ========================================

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        assertTrue(exception.getMessage().contains("999"));
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(any());
    }
}