package com.arceno.orderapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arceno.orderapi.dto.ProductFormDTO;
import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.mapper.ProductMapper;
import com.arceno.orderapi.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
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

    @Transactional
    public ProductResponseDTO createProduct(ProductFormDTO productFormDTO) {
        log.info("Criando novo produto: {}", productFormDTO);
        var product = productMapper.toEntity(productFormDTO);
        var savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductFormDTO productFormDTO) {
        log.info("Atualizando produto com id: {}. Novos dados: {}", id, productFormDTO);
        var existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        productMapper.updateEntityFromForm(productFormDTO, existingProduct);

        var updatedProduct = productRepository.save(existingProduct);
        return productMapper.toResponseDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deletando produto com id: {}", id);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com id: " + id);
        }

        productRepository.deleteById(id);

    }
}