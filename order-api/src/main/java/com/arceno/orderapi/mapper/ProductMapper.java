package com.arceno.orderapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "price", expression = "java(product.getPrice() != null ? product.getPrice().toString() : null)")
    ProductResponseDTO toResponseDTO(Product product);

}