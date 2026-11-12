package com.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}