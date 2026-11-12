package com.swiftcart.service;

import java.util.List;
import java.util.stream.Collectors;

import com.swiftcart.dto.request.CategoryRequest;
import com.swiftcart.dto.response.CategoryResponse;
import com.myProject.exception.*;
import com.swiftcart.exception.DuplicateResourceException;
import com.swiftcart.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.swiftcart.entity.Category;
import com.swiftcart.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("categories")
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                            .stream()
                            .map(category -> modelMapper.map(category, CategoryResponse.class))
                            .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        return modelMapper.map(category, CategoryResponse.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SELLER')")
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#name.toLowerCase()")
    public List<CategoryResponse> getCategoryByNameContainingIgnoreCase(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + categoryRequest.getName());
        }
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
        category.setName(categoryRequest.getName());
        return modelMapper.map(categoryRepository.save(category), CategoryResponse.class);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
