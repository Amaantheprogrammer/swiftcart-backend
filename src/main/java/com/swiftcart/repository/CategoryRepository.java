package com.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swiftcart.entity.Category;

import java.util.*;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
