package com.ecommerce.catalog.mapper;

import com.ecommerce.catalog.dto.ProductRequestDTO;
import com.ecommerce.catalog.dto.ProductResponseDTO;
import com.ecommerce.catalog.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Product (entidad) y DTOs.
 * Maneja la transformación bidireccional de datos.
 */
@Component
public class ProductMapper {

    /**
     * Convierte un ProductRequestDTO a Product (entidad).
     * Establece valores por defecto para campos opcionales.
     */
    public Product toEntity(ProductRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setCategoryId(dto.getCategoryId());
        product.setSku(dto.getSku());
        product.setActive(dto.getActive() != null ? dto.getActive() : true);
        return product;
    }

    /**
     * Convierte un Product (entidad) a ProductResponseDTO.
     * Incluye todos los campos incluyendo auditoría.
     */
    public ProductResponseDTO toResponseDTO(Product entity) {
        if (entity == null) {
            return null;
        }
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setCategoryId(entity.getCategoryId());
        dto.setSku(entity.getSku());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Actualiza una entidad Product existente con datos del DTO.
     * Preserva el ID y los timestamps.
     */
    public void updateEntityFromDTO(ProductRequestDTO dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());

        if (dto.getStock() != null) {
            entity.setStock(dto.getStock());
        }
        if (dto.getCategoryId() != null) {
            entity.setCategoryId(dto.getCategoryId());
        }
        if (dto.getSku() != null) {
            entity.setSku(dto.getSku());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
