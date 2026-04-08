package com.arceno.orderapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arceno.orderapi.dto.ProductFormDTO;
import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/products")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductController {
    ProductService productService;

    @GetMapping
    public Page<ProductResponseDTO> allProduct(Pageable pageable) {
        return productService.allProducts(pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ProductResponseDTO createProduct(@RequestBody ProductFormDTO productFormDTO) {
        return productService.createProduct(productFormDTO);
    }

}
