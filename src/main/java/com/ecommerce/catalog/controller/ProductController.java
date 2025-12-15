package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // -------------------------------------------------------------
    // GET (Listar y Buscar con Paginación)
    // -------------------------------------------------------------
    @Operation(summary = "Lista todos los productos, con paginación, ordenación y búsqueda por palabra clave.")
    @ApiResponse(responseCode = "200", description = "Lista de productos paginada.")
    @GetMapping
    public Page<ProductResponseDTO> getAllProducts(
            HttpServletRequest request,
            @RequestParam(required = false) String keyword, // Parámetro para búsqueda
            @RequestParam(value = "categoryId", required = false) Long categoryId, // Parámetro para filtrado por
                                                                                   // categoría explícito
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) @NonNull Pageable pageable) {

        log.info("DEBUG CRITICO - Query String recibida: '{}'", request.getQueryString());
        log.info("DEBUG CRITICO - Params: keyword='{}', categoryId='{}'", keyword, categoryId);

        Page<ProductResponseDTO> result;

        // Lógica de filtrado y búsqueda
        if (categoryId != null) {
            result = productService.getProductsByCategory(categoryId, pageable);
            log.info("Category filter completed - Found {} products for categoryId: {}", result.getTotalElements(),
                    categoryId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            result = productService.searchProducts(keyword, pageable);
            log.info("Search completed - Found {} products for keyword: {}", result.getTotalElements(), keyword);
        } else {
            result = productService.getAllProducts(pageable);
            log.info("Listing completed - Total products: {}", result.getTotalElements());
        }

        return result;
    }

    // Nuevo endpoint específico para categorías (Plan Z)
    @GetMapping("/category/{categoryId}")
    public Page<ProductResponseDTO> getProductsByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) @NonNull Pageable pageable) {

        log.info("GET /api/v1/products/category/{} - Filtering by category explicitly", categoryId);
        return productService.getProductsByCategory(categoryId, pageable);
    }

    // -------------------------------------------------------------
    // GET por ID
    // -------------------------------------------------------------
    @Operation(summary = "Obtiene un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        log.info("GET /api/v1/products/{} - Fetching product by ID", id);
        ProductResponseDTO product = productService.getProductById(id);
        log.debug("Product found: {}", product.getName());
        return product;
    }

    // -------------------------------------------------------------
    // POST (Crear Producto)
    // -------------------------------------------------------------
    @Operation(summary = "Crea un nuevo producto en el catálogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (Validación fallida)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO productDto) {
        log.info("POST /api/v1/products - Creating new product: {}", productDto.getName());
        log.debug("Product details - name: {}, price: {}", productDto.getName(), productDto.getPrice());

        ProductResponseDTO createdProduct = productService.createProduct(productDto);
        log.info("Product created successfully with ID: {}", createdProduct.getId());

        return createdProduct;
    }

    // -------------------------------------------------------------
    // PUT (Actualizar Producto)
    // -------------------------------------------------------------
    @Operation(summary = "Actualiza un producto existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (Validación fallida)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productDetails) {
        log.info("PUT /api/v1/products/{} - Updating product", id);
        log.debug("Update details - name: {}, price: {}", productDetails.getName(), productDetails.getPrice());

        ProductResponseDTO updatedProduct = productService.updateProduct(id, productDetails);
        log.info("Product {} updated successfully", id);

        return updatedProduct;
    }

    // -------------------------------------------------------------
    // DELETE (Eliminar Producto)
    // -------------------------------------------------------------
    @Operation(summary = "Elimina un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado con éxito (Sin Contenido)"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        log.info("Product {} deleted successfully", id);
    }
}