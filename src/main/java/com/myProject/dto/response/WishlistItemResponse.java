package com.myProject.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistItemResponse {
    private Long id;
    private Long productId;
    private Long productName;
    private Long productPrice;
    private Long addedAt;
}
