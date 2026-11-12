package com.swiftcart.dto.request;

import java.util.List;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductBatchRequest {
    private List<Long> productIds;
}
