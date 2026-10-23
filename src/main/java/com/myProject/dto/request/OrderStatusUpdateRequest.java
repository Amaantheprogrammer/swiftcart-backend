package com.myProject.dto.request;

import com.myProject.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
}
