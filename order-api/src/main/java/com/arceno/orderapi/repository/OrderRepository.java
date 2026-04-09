package com.arceno.orderapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arceno.orderapi.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
