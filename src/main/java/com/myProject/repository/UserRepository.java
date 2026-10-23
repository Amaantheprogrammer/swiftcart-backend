package com.myProject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;

import com.myProject.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Create own method besides the in-built ones(findAll, findById, deleteAll, etc)
    // Refer for possible methods: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
    @EntityGraph(attributePaths = {"cart", "wishlist"})
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
}