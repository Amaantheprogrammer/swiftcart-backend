package com.swiftcart.dto.request;

import com.swiftcart.enums.PaymentMethod;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;
    @Email
    private String email;
    private PaymentMethod paymentMethod;
}
