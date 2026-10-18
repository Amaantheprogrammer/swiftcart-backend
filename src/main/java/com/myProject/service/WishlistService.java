package com.myProject.service;

import com.myProject.entity.Wishlist;
import com.myProject.entity.WishlistItem;
import com.myProject.exception.*;
import com.myProject.entity.Product;
import com.myProject.repository.ProductRepository;
import com.myProject.util.CurrentUserUtil;
import com.myProject.entity.User;
import com.myProject.dto.response.WishlistResponse;
import com.myProject.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CurrentUserUtil currentUserUtil;

    @Transactional(readOnly = true)
    public WishlistResponse getMyWishlist() {
        Wishlist wishlist = getOrCreateWishlist();
        return modelMapper.map(wishlist, WishlistResponse.class);
    }

    @Transactional
    public WishlistResponse addProductToMyWishlist(Long productId) {
        Wishlist wishlist = getOrCreateWishlist();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        boolean productExists = wishlist.getWishlistItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
        if (productExists) {
            throw new DuplicateResourceException("Product already exists with ID: " + productId);
        }
        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();
        wishlist.getWishlistItems().add(wishlistItem);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return modelMapper.map(savedWishlist, WishlistResponse.class);
    }

    @Transactional
    public void removeProductFromMyWishlist(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with ID: " + productId);
        }
        Wishlist wishlist = currentUserUtil.getCurrentUser().getWishlist();
        wishlist.getWishlistItems().removeIf(
                wishlistItem -> wishlistItem.getProduct().getId().equals(productId)
        );
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void clearWishlist() {
        Wishlist wishlist = getOrCreateWishlist();
        if (wishlist.getWishlistItems().isEmpty()) {
            return;
        }
        wishlist.getWishlistItems().clear();
        wishlistRepository.save(wishlist);
    }

    // Private method
    private Wishlist getOrCreateWishlist() {
        User user = currentUserUtil.getCurrentUser();
        Wishlist wishlist = user.getWishlist();
        if (wishlist == null) {
            wishlist = Wishlist.builder()
                    .user(user)
                    .build();
            wishlist = wishlistRepository.save(wishlist);
            user.setWishlist(wishlist);
        }
        return wishlist;
    }
}
