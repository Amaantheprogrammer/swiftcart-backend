package com.myProject.service;

import com.myProject.dto.request.TransferRequest;
import com.myProject.dto.response.AccountResponse;
import com.myProject.dto.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DigitalBankingClientService {

    private final RestClient restClient;

    public AccountResponse getAccountByNumber(String accountNumber) {
        String token = getToken();
        return restClient.get()
                .uri("/accounts/{accountNumber}", accountNumber)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(AccountResponse.class);
    }

    public void transfer(TransferRequest request) {
        String token = getToken();
        restClient.post()
                .uri("/transactions/transfer")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private String getToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        throw new AuthenticationCredentialsNotFoundException("Token not found");
    }

}
