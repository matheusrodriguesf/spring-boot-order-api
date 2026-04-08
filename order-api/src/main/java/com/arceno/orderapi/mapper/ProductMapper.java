package com.arceno.orderapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.arceno.orderapi.dto.ProductFormDTO;
import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    default Product toEntity(ProductFormDTO productFormDTO) {
        if (productFormDTO == null) {
            return null;
        }

        return Product.builder()
                .name(productFormDTO.name())
                .description(productFormDTO.description())
                .price(productFormDTO.price())
                .stock(productFormDTO.stock())
                .build();
    }

    @Mapping(target = "price", expression = "java(product.getPrice() != null ? product.getPrice().toString() : null)")
    ProductResponseDTO toResponseDTO(Product product);

}