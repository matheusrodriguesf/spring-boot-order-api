package com.arceno.orderapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arceno.orderapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
