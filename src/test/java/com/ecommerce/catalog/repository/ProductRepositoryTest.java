package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para ProductRepository.
 * Utiliza @DataJpaTest para configurar solo la capa de persistencia.
 * Utiliza H2 en memoria para las pruebas.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product laptop;
    private Product mouse;
    private Product keyboard;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        productRepository.deleteAll();

        // Crear productos de prueba
        laptop = createTestProduct("Laptop Gaming", "Laptop para juegos con RTX", BigDecimal.valueOf(1500.0),
                "LAP-001");
        mouse = createTestProduct("Mouse Gaming", "Mouse RGB 5 botones", BigDecimal.valueOf(50.0), "MOU-001");
        keyboard = createTestProduct("Teclado Mecánico", "Teclado con switches azules", BigDecimal.valueOf(80.0),
                "KEY-001");
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
    }

    // ========================================
    // Método Helper
    // ========================================

    /**
     * Crea un producto de prueba con todos los campos requeridos.
     */
    private Product createTestProduct(String name, String description, BigDecimal price, String sku) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(10);
        product.setSku(sku);
        product.setActive(true);
        return product;
    }

    // ========================================
    // Tests para operaciones CRUD básicas
    // ========================================

    @Test
    void testSaveProduct() {
        Product savedProduct = productRepository.save(Objects.requireNonNull(laptop));

        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("Laptop Gaming", savedProduct.getName());
        assertEquals(0, BigDecimal.valueOf(1500.0).compareTo(savedProduct.getPrice()));
    }

    @Test
    void testFindById() {
        Product savedProduct = productRepository.save(Objects.requireNonNull(laptop));

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Laptop Gaming", foundProduct.get().getName());
        assertEquals(savedProduct.getId(), foundProduct.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Product> foundProduct = productRepository.findById(999L);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testUpdateProduct() {
        Product savedProduct = productRepository.save(Objects.requireNonNull(laptop));

        // Actualizar el producto
        savedProduct.setName("Laptop Gaming Actualizada");
        savedProduct.setPrice(BigDecimal.valueOf(1800.0));
        Product updatedProduct = productRepository.save(savedProduct);

        assertEquals("Laptop Gaming Actualizada", updatedProduct.getName());
        assertEquals(0, BigDecimal.valueOf(1800.0).compareTo(updatedProduct.getPrice()));
        assertEquals(savedProduct.getId(), updatedProduct.getId());
    }

    @Test
    void testDeleteProduct() {
        Product savedProduct = productRepository.save(Objects.requireNonNull(laptop));
        Long productId = savedProduct.getId();

        productRepository.deleteById(productId);

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    void testExistsById() {
        Product savedProduct = productRepository.save(Objects.requireNonNull(laptop));

        assertTrue(productRepository.existsById(savedProduct.getId()));
        assertFalse(productRepository.existsById(999L));
    }

    @Test
    void testFindAll() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));
        productRepository.save(Objects.requireNonNull(keyboard));

        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        assertEquals(3, products.size());
    }

    // ========================================
    // Tests para búsqueda personalizada
    // ========================================

    @Test
    void testFindByNameContaining() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));
        productRepository.save(Objects.requireNonNull(keyboard));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "Gaming", "Gaming", pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Laptop Gaming")));
        assertTrue(result.getContent().stream().anyMatch(p -> p.getName().equals("Mouse Gaming")));
    }

    @Test
    void testFindByDescriptionContaining() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));
        productRepository.save(Objects.requireNonNull(keyboard));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "switches", "switches", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Teclado Mecánico", result.getContent().get(0).getName());
    }

    @Test
    void testFindCaseInsensitive() {
        productRepository.save(Objects.requireNonNull(laptop));

        PageRequest pageable = PageRequest.of(0, 10);

        // Buscar con minúsculas
        Page<Product> result1 = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "laptop", "laptop", pageable);
        assertEquals(1, result1.getTotalElements());

        // Buscar con mayúsculas
        Page<Product> result2 = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "LAPTOP", "LAPTOP", pageable);
        assertEquals(1, result2.getTotalElements());

        // Buscar con mezcla
        Page<Product> result3 = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "LaPtOp", "LaPtOp", pageable);
        assertEquals(1, result3.getTotalElements());
    }

    @Test
    void testFindWithNoResults() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "NoExiste", "NoExiste", pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    // ========================================
    // Tests para paginación y ordenamiento
    // ========================================

    @Test
    void testFindAllWithPagination() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));
        productRepository.save(Objects.requireNonNull(keyboard));

        // Página 0, tamaño 2
        PageRequest pageable = PageRequest.of(0, 2);
        Page<Product> page = productRepository.findAll(pageable);

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertTrue(page.hasNext());
    }

    @Test
    void testFindAllWithSorting() {
        productRepository.save(createTestProduct("Zebra", "Producto Z", BigDecimal.valueOf(100.0), "ZEB-001"));
        productRepository.save(createTestProduct("Alpha", "Producto A", BigDecimal.valueOf(200.0), "ALP-001"));
        productRepository.save(createTestProduct("Beta", "Producto B", BigDecimal.valueOf(150.0), "BET-001"));

        // Ordenar por nombre ascendente
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> page = productRepository.findAll(pageable);

        assertEquals(3, page.getTotalElements());
        assertEquals("Alpha", page.getContent().get(0).getName());
        assertEquals("Beta", page.getContent().get(1).getName());
        assertEquals("Zebra", page.getContent().get(2).getName());
    }

    @Test
    void testFindAllWithSortingDescending() {
        productRepository.save(createTestProduct("Producto A", "Desc A", BigDecimal.valueOf(100.0), "PRO-A"));
        productRepository.save(createTestProduct("Producto B", "Desc B", BigDecimal.valueOf(200.0), "PRO-B"));
        productRepository.save(createTestProduct("Producto C", "Desc C", BigDecimal.valueOf(150.0), "PRO-C"));

        // Ordenar por precio descendente
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("price").descending());
        Page<Product> page = productRepository.findAll(pageable);

        assertEquals(3, page.getTotalElements());
        assertEquals(0, BigDecimal.valueOf(200.0).compareTo(page.getContent().get(0).getPrice()));
        assertEquals(0, BigDecimal.valueOf(150.0).compareTo(page.getContent().get(1).getPrice()));
        assertEquals(0, BigDecimal.valueOf(100.0).compareTo(page.getContent().get(2).getPrice()));
    }

    @Test
    void testSearchWithPagination() {
        // Crear 5 productos con "Gaming" en el nombre
        for (int i = 1; i <= 5; i++) {
            productRepository.save(createTestProduct("Gaming Product " + i, "Description " + i,
                    BigDecimal.valueOf(100.0 * i), "GAM-00" + i));
        }

        PageRequest pageable = PageRequest.of(0, 3);
        Page<Product> page = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                "Gaming", "Gaming", pageable);

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertTrue(page.hasNext());
    }

    // ========================================
    // Tests de casos especiales
    // ========================================

    @Test
    void testSaveProductWithNullId() {
        Product product = createTestProduct("Test Product", "Test Description", BigDecimal.valueOf(100.0), "TEST-001");
        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertTrue(savedProduct.getId() > 0);
    }

    @Test
    void testDeleteAll() {
        productRepository.save(Objects.requireNonNull(laptop));
        productRepository.save(Objects.requireNonNull(mouse));
        productRepository.save(Objects.requireNonNull(keyboard));

        assertEquals(3, productRepository.count());

        productRepository.deleteAll();

        assertEquals(0, productRepository.count());
    }

    @Test
    void testCount() {
        assertEquals(0, productRepository.count());

        productRepository.save(Objects.requireNonNull(laptop));
        assertEquals(1, productRepository.count());

        productRepository.save(Objects.requireNonNull(mouse));
        assertEquals(2, productRepository.count());

        productRepository.save(Objects.requireNonNull(keyboard));
        assertEquals(3, productRepository.count());
    }
}
