package com.ecommerce.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el catálogo de e-commerce.
 * Incluye información básica, inventario, categorización y auditoría.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "productos", indexes = {
        // Índice para búsquedas por nombre (usado en searchProducts)
        @Index(name = "idx_product_name", columnList = "name"),

        // Índice para búsquedas por SKU (campo único, pero mejora lookups)
        @Index(name = "idx_product_sku", columnList = "sku"),

        // Índice para filtros por categoría (FK, muy usado en filtros)
        @Index(name = "idx_product_category", columnList = "category_id"),

        // Índice para filtros por estado activo/inactivo
        @Index(name = "idx_product_active", columnList = "active"),

        // Índice compuesto para búsquedas de productos activos por categoría
        // (útil para queries como: WHERE active=true AND category_id=?)
        @Index(name = "idx_product_active_category", columnList = "active, category_id")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ========================================
    // Campos de Inventario y Categorización
    // ========================================

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    @Column(unique = true, length = 50, nullable = false)
    private String sku;

    @Column(nullable = false)
    private Boolean active = true;

    // ========================================
    // Campos de Auditoría
    // ========================================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}