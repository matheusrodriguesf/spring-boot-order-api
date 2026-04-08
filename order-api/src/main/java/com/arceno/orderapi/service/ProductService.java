package com.arceno.orderapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.arceno.orderapi.dto.ProductFormDTO;
import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.mapper.ProductMapper;
import com.arceno.orderapi.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;

    public Page<ProductResponseDTO> allProducts(Pageable pageable) {
        log.info("Buscando todos os produtos com paginação: {}", pageable);
        return productRepository.findAll(pageable)
                .map(productMapper::toResponseDTO);

    }

    public ProductResponseDTO getProductById(Long id) {
        log.info("Buscando produto com id: {}", id);
        return productRepository.findById(id)
                .map(productMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
    }

    public ProductResponseDTO createProduct(ProductFormDTO productFormDTO) {
        log.info("Criando novo produto: {}", productFormDTO);
        var product = productMapper.toEntity(productFormDTO);
        var savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

}
