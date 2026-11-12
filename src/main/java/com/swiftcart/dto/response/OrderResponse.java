package com.swiftcart.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.swiftcart.enums.OrderStatus;
import com.swiftcart.enums.PaymentStatus;
import com.swiftcart.enums.PaymentMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
}