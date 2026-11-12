package com.swiftcart.dto.request;

import com.swiftcart.enums.OrderStatus;
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
