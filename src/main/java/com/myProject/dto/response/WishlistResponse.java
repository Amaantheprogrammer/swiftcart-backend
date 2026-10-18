package com.myProject.dto.response;

import lombok.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponse {
    private Long id;
    private Long userId;
    private List<WishlistItemResponse> wishlistItemResponses;
}
