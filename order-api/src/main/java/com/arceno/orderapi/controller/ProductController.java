package com.arceno.orderapi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.arceno.orderapi.dto.ProductFormDTO;
import com.arceno.orderapi.dto.ProductResponseDTO;
import com.arceno.orderapi.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@Validated
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
    public ProductResponseDTO getProductById(@PathVariable @Positive(message = "Id deve ser maior que zero") Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ProductResponseDTO createProduct(@RequestBody @Valid ProductFormDTO productFormDTO) {
        return productService.createProduct(productFormDTO);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO updateProduct(@PathVariable @Positive(message = "Id deve ser maior que zero") Long id,
            @RequestBody @Valid ProductFormDTO productFormDTO) {
        return productService.updateProduct(id, productFormDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable @Positive(message = "Id deve ser maior que zero") Long id) {
        productService.deleteProduct(id);
    }

}
