package com.myProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myProject.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}