package com.myProject.controller;

import com.myProject.dto.response.WishlistResponse;
import com.myProject.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlists", description = "Wishlist APIs")
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    @Operation(
            summary = "Get my wish list"
    )
    public ResponseEntity<WishlistResponse> getMyWishlist() {
        return ResponseEntity.ok(wishlistService.getMyWishlist());
    }

    @PostMapping("/{productId}")
    @Operation(
            summary = "Add product to my wish list"
    )
    public ResponseEntity<WishlistResponse> addProductToMyWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addProductToMyWishlist(productId));
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Remove product to my wish list"
    )
    public ResponseEntity<Void> removeProductFromMyWishlist(@PathVariable Long productId) {
        wishlistService.removeProductFromMyWishlist(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(
            summary = "Clear my wish list"
    )
    public ResponseEntity<Void> clearWishlist() {
        wishlistService.clearWishlist();
        return ResponseEntity.noContent().build();
    }
}
