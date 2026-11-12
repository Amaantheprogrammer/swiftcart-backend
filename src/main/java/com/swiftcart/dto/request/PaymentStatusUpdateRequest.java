package com.swiftcart.dto.request;

import com.swiftcart.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusUpdateRequest {
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}
