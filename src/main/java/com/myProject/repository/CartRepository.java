package com.myProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myProject.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}