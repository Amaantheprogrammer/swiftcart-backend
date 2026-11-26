package com.swiftcart.dto;

import java.math.BigDecimal;

public record OrderEmailDetails(
    Long orderId,
    String customerName,
    String customerEmail,
    BigDecimal totalAmount
) {
}
