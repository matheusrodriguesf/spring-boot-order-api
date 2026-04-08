package com.arceno.orderapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arceno.orderapi.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
