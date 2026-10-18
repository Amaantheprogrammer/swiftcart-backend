package com.myProject.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Builder
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}