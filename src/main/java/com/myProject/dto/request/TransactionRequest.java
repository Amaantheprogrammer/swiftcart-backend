package com.myProject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    @NotNull(message = "Account number is required")
    private String accountNumber;
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
