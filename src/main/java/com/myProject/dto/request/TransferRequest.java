package com.myProject.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    private String customerAccountNumber;
    private String sellerAccountNumber;
    private BigDecimal amount;
}
