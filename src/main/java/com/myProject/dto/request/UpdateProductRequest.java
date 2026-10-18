package com.myProject.dto.request;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequest {
    private String name;
    private BigDecimal price;
    private String description;
    private Integer stockQuantity;
}