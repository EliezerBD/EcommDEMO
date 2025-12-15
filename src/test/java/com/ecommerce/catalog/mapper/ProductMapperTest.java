package com.ecommerce.catalog.mapper;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ProductMapper.
 * Valida la correcta transformación entre DTOs y entidades.
 */
class ProductMapperTest {

    private ProductMapper productMapper;
    private Product product;
    private ProductRequestDTO productRequestDTO;
    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Laptop Core i7");
        product.setPrice(BigDecimal.valueOf(1200.0));
        product.setStock(10);
        product.setCategoryId(1L);
        product.setSku("LAP-001");
        product.setActive(true);

        productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setName("Mouse Gaming");
        productRequestDTO.setDescription("Mouse RGB 5 botones");
        productRequestDTO.setPrice(BigDecimal.valueOf(50.0));
        productRequestDTO.setStock(20);
        productRequestDTO.setCategoryId(2L);
        productRequestDTO.setSku("MOU-001");
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
    }

    // ========================================
    // Tests para toEntity
    // ========================================

    @Test
    void testToEntityWithValidData() {
        Product result = productMapper.toEntity(productRequestDTO);

        assertNotNull(result);
        assertNull(result.getId()); // El ID no debe ser asignado desde el DTO
        assertEquals("Mouse Gaming", result.getName());
        assertEquals("Mouse RGB 5 botones", result.getDescription());
        assertEquals(BigDecimal.valueOf(50.0), result.getPrice());
        assertEquals(20, result.getStock());
        assertEquals(2L, result.getCategoryId());
        assertEquals("MOU-001", result.getSku());
        assertTrue(result.getActive());
    }

    @Test
    void testToEntityWithNullDTO() {
        Product result = productMapper.toEntity(null);

        assertNull(result);
    }

    // ========================================
    // Tests para toResponseDTO
    // ========================================

    @Test
    void testToResponseDTOWithValidData() {
        ProductResponseDTO result = productMapper.toResponseDTO(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals("Laptop Core i7", result.getDescription());
        assertEquals(BigDecimal.valueOf(1200.0), result.getPrice());
        assertEquals(10, result.getStock());
        assertEquals(1L, result.getCategoryId());
        assertEquals("LAP-001", result.getSku());
        assertTrue(result.getActive());
    }

    @Test
    void testToResponseDTOWithNullEntity() {
        ProductResponseDTO result = productMapper.toResponseDTO(null);

        assertNull(result);
    }

    @Test
    void testToResponseDTOWithAllFields() {
        // Verificar que todos los campos se mapean correctamente
        Product fullProduct = new Product();
        fullProduct.setId(99L);
        fullProduct.setName("Teclado Mecánico");
        fullProduct.setDescription("Teclado RGB switches azules");
        fullProduct.setPrice(BigDecimal.valueOf(150.0));
        fullProduct.setStock(50);
        fullProduct.setCategoryId(3L);
        fullProduct.setSku("KEY-001");
        fullProduct.setActive(false);

        ProductResponseDTO result = productMapper.toResponseDTO(fullProduct);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Teclado Mecánico", result.getName());
        assertEquals("Teclado RGB switches azules", result.getDescription());
        assertEquals(BigDecimal.valueOf(150.0), result.getPrice());
        assertEquals(50, result.getStock());
        assertEquals(3L, result.getCategoryId());
        assertEquals("KEY-001", result.getSku());
        assertFalse(result.getActive());
    }

    // ========================================
    // Tests para updateEntityFromDTO
    // ========================================

    @Test
    void testUpdateEntityFromDTO() {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Laptop Actualizada");
        updateDTO.setDescription("Laptop Core i9");
        updateDTO.setPrice(BigDecimal.valueOf(1500.0));
        updateDTO.setStock(15);
        updateDTO.setCategoryId(2L);
        updateDTO.setSku("LAP-002");
        updateDTO.setActive(false);

        productMapper.updateEntityFromDTO(updateDTO, product);

        // El ID debe permanecer sin cambios
        assertEquals(1L, product.getId());
        // Los demás campos deben actualizarse
        assertEquals("Laptop Actualizada", product.getName());
        assertEquals("Laptop Core i9", product.getDescription());
        assertEquals(BigDecimal.valueOf(1500.0), product.getPrice());
        assertEquals(15, product.getStock());
        assertEquals(2L, product.getCategoryId());
        assertEquals("LAP-002", product.getSku());
        assertFalse(product.getActive());
    }

    @Test
    void testUpdateEntityPreservesId() {
        Long originalId = product.getId();

        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Nuevo Nombre");
        updateDTO.setDescription("Nueva Descripción");
        updateDTO.setPrice(BigDecimal.valueOf(999.0));

        productMapper.updateEntityFromDTO(updateDTO, product);

        // Verificar que el ID no cambió
        assertEquals(originalId, product.getId());
        assertNotNull(product.getId());
    }

    @Test
    void testUpdateEntityFromDTOWithNullDTO() {
        Long originalId = product.getId();
        String originalName = product.getName();
        String originalDescription = product.getDescription();
        BigDecimal originalPrice = product.getPrice();

        productMapper.updateEntityFromDTO(null, product);

        // Si el DTO es null, la entidad no debe cambiar
        assertEquals(originalId, product.getId());
        assertEquals(originalName, product.getName());
        assertEquals(originalDescription, product.getDescription());
        assertEquals(originalPrice, product.getPrice());
    }

    @Test
    void testUpdateEntityFromDTOWithNullEntity() {
        // Verificar que no lance excepción cuando la entidad es null
        assertDoesNotThrow(() -> productMapper.updateEntityFromDTO(productRequestDTO, null));
    }

    // ========================================
    // Tests de integración entre métodos
    // ========================================

    @Test
    void testRoundTripConversion() {
        // DTO -> Entity -> DTO
        Product entity = productMapper.toEntity(productRequestDTO);
        entity.setId(10L); // Simular que fue guardado en BD

        ProductResponseDTO responseDTO = productMapper.toResponseDTO(entity);

        assertNotNull(responseDTO);
        assertEquals(10L, responseDTO.getId());
        assertEquals(productRequestDTO.getName(), responseDTO.getName());
        assertEquals(productRequestDTO.getDescription(), responseDTO.getDescription());
        assertEquals(productRequestDTO.getPrice(), responseDTO.getPrice());
    }
}
