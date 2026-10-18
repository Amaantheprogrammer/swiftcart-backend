package com.myProject.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageRequest {
        @NotBlank(message = "Image URL is a required field")
        private String imageUrl;
    }
}