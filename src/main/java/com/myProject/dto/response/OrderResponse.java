package com.myProject.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.myProject.enums.OrderStatus;
import com.myProject.enums.PaymentStatus;
import com.myProject.enums.PaymentMethod;

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