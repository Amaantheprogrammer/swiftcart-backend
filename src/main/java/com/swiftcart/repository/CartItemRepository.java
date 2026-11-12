package com.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.swiftcart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}