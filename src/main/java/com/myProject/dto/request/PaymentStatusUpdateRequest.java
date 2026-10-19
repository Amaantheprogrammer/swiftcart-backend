package com.myProject.dto.request;

import com.myProject.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentStatusUpdateRequest {
    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}
