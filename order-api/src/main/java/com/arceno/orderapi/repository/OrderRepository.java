package com.arceno.orderapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arceno.orderapi.entity.Order;
import com.arceno.orderapi.enumeration.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

}
