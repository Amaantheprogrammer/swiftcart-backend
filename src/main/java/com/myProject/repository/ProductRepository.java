package com.myProject.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.myProject.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE p.approved")
    Page<Product> findAllApprovedProducts(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE NOT p.approved")
    Page<Product> findPendingProducts(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p")
    Page<Product> findAllProductsAdmin(Pageable pageable);
    
    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.price < :price")
    List<Product> findByNameContainingIgnoreCaseAndPriceLessThan(@Param("name") String name, @Param("price") BigDecimal price);
    
    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE p.category.id = :id")
    List<Product> findProductsByCategoryId(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE p.category.id = :id AND p.price < :price")
    List<Product> findByCategoryIdAndPriceLessThan(@Param("id") Long id, @Param("price") BigDecimal price);
    
    @EntityGraph(attributePaths = {"category", "productImages", "user"})
    @Query("SELECT p FROM Product p WHERE p.id IN :ids AND p.approved")
    List<Product> findByIdInAndApproved(List<Long> ids);
    
}