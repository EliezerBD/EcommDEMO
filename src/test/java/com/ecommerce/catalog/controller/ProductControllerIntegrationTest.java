package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.entity.Product;
import com.ecommerce.catalog.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
// ¡Import Correcto! Permite usar user()
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de integración para ProductController.
 * Utiliza @SpringBootTest para levantar el contexto completo de Spring.
 * Utiliza H2 en memoria para las pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada prueba
        productRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // Limpiar la base de datos después de cada prueba
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
    // Tests para POST /api/v1/products (Ahora REQUIERE AUTENTICACIÓN)
    // ========================================

    @Test
    void testCreateProductAndRetrieveIt() throws Exception {
        // 1. Objeto a enviar (DTO)
        ProductRequestDTO newProduct = new ProductRequestDTO();
        newProduct.setName("Mouse Gaming");
        newProduct.setDescription("Mouse con 5 botones");
        newProduct.setPrice(BigDecimal.valueOf(50.0));
        newProduct.setStock(10);
        newProduct.setSku("MOU-001");
        newProduct.setActive(true);

        // 2. Simular POST (Crear) - AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(post("/api/v1/products")
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Mouse Gaming")))
                .andExpect(jsonPath("$.price", is(50.0)));

        // 3. Simular GET (Verificar que se creó) - Este es público
        mockMvc.perform(get("/api/v1/products?keyword=Gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Mouse Gaming")));
    }

    @Test
    void testCreateProductValidationFails() throws Exception {
        // Objeto INVÁLIDO (Precio negativo)
        ProductRequestDTO invalidProduct = new ProductRequestDTO();
        invalidProduct.setName("Producto Malo");
        invalidProduct.setDescription("Desc");
        invalidProduct.setPrice(BigDecimal.valueOf(-10.0));
        invalidProduct.setSku("BAD-001");

        // Simular POST con validación fallida - AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(post("/api/v1/products")
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.price", is("El precio del producto debe ser positivo")));
    }

    // ========================================
    // NUEVO TEST DE INTEGRACIÓN: SKU DUPLICADO (409 CONFLICT)
    // ========================================
    @Test
    void testCreateProductDuplicateSkuFailsAndReturnsConflict409() throws Exception {
        // 1. Configuración: Guardar un producto que usará el SKU de conflicto
        final String CONFLICT_SKU = "SKU-DUP";
        productRepository.save(
                createTestProduct("Original Product", "Original Desc", BigDecimal.TEN, CONFLICT_SKU));

        assertEquals(1, productRepository.count());

        // 2. Preparar el DTO de la petición con el SKU duplicado
        ProductRequestDTO duplicateSkuDTO = new ProductRequestDTO();
        duplicateSkuDTO.setName("Duplicado");
        duplicateSkuDTO.setDescription("Fallo esperado");
        duplicateSkuDTO.setPrice(BigDecimal.valueOf(100.0));
        duplicateSkuDTO.setSku(CONFLICT_SKU); // ESTO CAUSA EL 409

        // 3. Simular la petición POST y verificar el resultado - AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(post("/api/v1/products")
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateSkuDTO)))

                // 4. Verificaciones
                .andExpect(status().isConflict()) // Debe devolver el código de estado 409 CONFLICT
                .andExpect(jsonPath("$.error").value("Conflicto de Integridad de Datos"))
                .andExpect(jsonPath("$.message", containsString("SKU")));

        // 5. Verificación de Persistencia: Asegurarse de que el segundo producto NO fue guardado
        assertEquals(1, productRepository.count());
    }

    // ========================================
    // Tests para GET /api/v1/products (Públicos)
    // ========================================

    @Test
    void testGetAllProductsWithPagination() throws Exception {
        // Crear 3 productos
        productRepository.save(
                createTestProduct("Laptop", "Laptop Core i7", BigDecimal.valueOf(1200.0), "LAP-001"));
        productRepository.save(createTestProduct("Mouse", "Mouse óptico", BigDecimal.valueOf(25.0), "MOU-001"));
        productRepository.save(
                createTestProduct("Teclado", "Teclado mecánico", BigDecimal.valueOf(80.0), "KEY-001"));

        // Simular GET con paginación - PÚBLICO
        mockMvc.perform(get("/api/v1/products?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.number", is(0)));
    }

    // ... (El resto de los tests GET no necesitan autenticación) ...
    @Test
    void testGetAllProductsWithSorting() throws Exception {
        // Crear productos en orden aleatorio
        productRepository.save(createTestProduct("Zebra", "Producto Z", BigDecimal.valueOf(100.0), "ZEB-001"));
        productRepository.save(createTestProduct("Alpha", "Producto A", BigDecimal.valueOf(200.0), "ALP-001"));
        productRepository.save(createTestProduct("Beta", "Producto B", BigDecimal.valueOf(150.0), "BET-001"));

        // Simular GET con ordenamiento por nombre ascendente
        mockMvc.perform(get("/api/v1/products?sort=name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Alpha")))
                .andExpect(jsonPath("$.content[1].name", is("Beta")));
    }

    @Test
    void testGetAllProductsEmpty() throws Exception {
        // No crear productos

        // Simular GET
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void testSearchProductsByKeyword() throws Exception {
        // Crear productos
        productRepository.save(createTestProduct("Laptop Gaming", "Laptop para juegos",
                BigDecimal.valueOf(1500.0), "LAP-001"));
        productRepository.save(
                createTestProduct("Mouse Gaming", "Mouse RGB", BigDecimal.valueOf(50.0), "MOU-001"));
        productRepository.save(
                createTestProduct("Teclado", "Teclado normal", BigDecimal.valueOf(30.0), "KEY-001"));

        // Buscar por "Gaming"
        mockMvc.perform(get("/api/v1/products?keyword=Gaming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", hasItems("Laptop Gaming", "Mouse Gaming")));
    }

    @Test
    void testSearchProductsNoResults() throws Exception {
        // Crear un producto
        productRepository.save(
                createTestProduct("Laptop", "Laptop normal", BigDecimal.valueOf(1000.0), "LAP-001"));

        // Buscar algo que no existe
        mockMvc.perform(get("/api/v1/products?keyword=NoExiste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void testSearchProductsCaseInsensitive() throws Exception {
        // Crear producto
        productRepository.save(createTestProduct("LAPTOP", "Laptop mayúsculas", BigDecimal.valueOf(1000.0),
                "LAP-001"));

        // Buscar con minúsculas
        mockMvc.perform(get("/api/v1/products?keyword=laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }


    @Test
    void testGetProductByIdSuccess() throws Exception {
        Product savedProduct = productRepository.save(
                createTestProduct("Laptop", "Laptop Core i7", BigDecimal.valueOf(1200.0), "LAP-001"));

        mockMvc.perform(get("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1200.0)));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    // ========================================
    // Tests para PUT /api/v1/products/{id} (Ahora REQUIERE AUTENTICACIÓN)
    // ========================================

    @Test
    void testUpdateProductSuccess() throws Exception {
        Product savedProduct = productRepository.save(
                createTestProduct("Laptop", "Laptop Core i7", BigDecimal.valueOf(1200.0), "LAP-001"));

        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Laptop Actualizada");
        updateDTO.setDescription("Laptop Core i9");
        updateDTO.setPrice(BigDecimal.valueOf(1500.0));
        updateDTO.setStock(15);
        updateDTO.setSku("LAP-001");
        updateDTO.setActive(true);

        // AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(put("/api/v1/products/" + savedProduct.getId())
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop Actualizada")))
                .andExpect(jsonPath("$.price", is(1500.0)));

        // Verificar que se actualizó en la BD (GET es público)
        mockMvc.perform(get("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop Actualizada")));
    }

    @Test
    void testUpdateProductNotFound() throws Exception {
        ProductRequestDTO updateDTO = new ProductRequestDTO();
        updateDTO.setName("Producto");
        updateDTO.setDescription("Descripción");
        updateDTO.setPrice(BigDecimal.valueOf(100.0));
        updateDTO.setSku("PRO-001");

        // AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(put("/api/v1/products/999")
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProductValidationFails() throws Exception {
        Product savedProduct = productRepository.save(
                createTestProduct("Laptop", "Laptop Core i7", BigDecimal.valueOf(1200.0), "LAP-001"));

        ProductRequestDTO invalidUpdate = new ProductRequestDTO();
        invalidUpdate.setName(""); // Nombre vacío
        invalidUpdate.setDescription("Desc");
        invalidUpdate.setPrice(BigDecimal.valueOf(100.0));
        invalidUpdate.setSku("LAP-001");

        // AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(put("/api/v1/products/" + savedProduct.getId())
                        .with(user("admin").password("pass").roles("ADMIN")) // <-- AUTENTICACIÓN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());
    }

    // ========================================
    // Tests para DELETE /api/v1/products/{id} (Ahora REQUIERE AUTENTICACIÓN)
    // ========================================

    @Test
    void testDeleteProductSuccess() throws Exception {
        Product savedProduct = productRepository.save(
                createTestProduct("Laptop", "Laptop Core i7", BigDecimal.valueOf(1200.0), "LAP-001"));

        // AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(delete("/api/v1/products/" + savedProduct.getId())
                        .with(user("admin").password("pass").roles("ADMIN"))) // <-- AUTENTICACIÓN
                .andExpect(status().isNoContent());

        // Verificar que ya no existe (GET es público)
        mockMvc.perform(get("/api/v1/products/" + savedProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductNotFound() throws Exception {
        // AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(delete("/api/v1/products/999")
                        .with(user("admin").password("pass").roles("ADMIN"))) // <-- AUTENTICACIÓN
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductAndVerifyList() throws Exception {
        Product p1 = productRepository
                .save(createTestProduct("P1", "Desc1", BigDecimal.valueOf(100.0), "P1-001"));
        productRepository.save(createTestProduct("P2", "Desc2", BigDecimal.valueOf(200.0), "P2-001"));
        productRepository.save(createTestProduct("P3", "Desc3", BigDecimal.valueOf(300.0), "P3-001"));

        // Eliminar P1 - AÑADIMOS AUTENTICACIÓN
        mockMvc.perform(delete("/api/v1/products/" + p1.getId())
                        .with(user("admin").password("pass").roles("ADMIN"))) // <-- AUTENTICACIÓN
                .andExpect(status().isNoContent());

        // Verificar que solo quedan 2 (GET es público)
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)));
    }

    // ========================================
    // Tests de Seguridad (NUEVOS)
    // ========================================

    // Tests de Seguridad: Acceso Público (409)
    @Test
    void testGetAllProductsIsPublic()throws Exception{
        // Peticiones GET siguen siendo públicas (200 OK)
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // Tests de Seguridad: Acceso Denegado (401)
    @Test
    void testCreateProductWithoutAuthenticationFailsWith401()throws Exception{
        ProductRequestDTO newProduct = new ProductRequestDTO();
        newProduct.setName("Producto Secreto");
        newProduct.setDescription("Solo para admins");
        newProduct.setPrice(BigDecimal.valueOf(100.0));
        newProduct.setStock(5);
        newProduct.setSku("SEC-001");
        newProduct.setActive(true);

        // Simular POST sin autenticación. Esperamos 401.
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized

        // Verifica que no se aguardo ese producto
        assertEquals(0, productRepository.count());
    }

    // Tests de Seguridad: Acceso Exitoso (POST con Auth)
    @Test
    void testCreateProductWithAuthenticationSuccessSecurity() throws Exception { // Renombre para evitar conflicto
        ProductRequestDTO newProduct = new ProductRequestDTO();
        newProduct.setName("Producto Autenticado");
        newProduct.setDescription("Para pasar el test");
        newProduct.setPrice(BigDecimal.valueOf(200.0));
        newProduct.setStock(1);
        newProduct.setSku("AUTH-001");
        newProduct.setActive(true);

        // Usamos el usuario 'admin' configurado en SecurityConfig
        mockMvc.perform(post("/api/v1/products")
                        .with(user("admin").password("pass").roles("ADMIN")) // Simula un usuario con rol ADMIN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated());

        // Verificar que el producto SÍ fue guardado
        assertEquals(1, productRepository.count());
    }
}