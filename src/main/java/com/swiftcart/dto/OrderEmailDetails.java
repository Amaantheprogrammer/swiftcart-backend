package com.swiftcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderEmailDetails(
    Long orderId,
    String customerName,
    String customerEmail,
    BigDecimal totalAmount,
    LocalDateTime orderDate
) {
}
