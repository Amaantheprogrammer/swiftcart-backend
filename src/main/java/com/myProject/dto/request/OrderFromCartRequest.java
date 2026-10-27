package com.myProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderFromCartRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    private String address;
}
