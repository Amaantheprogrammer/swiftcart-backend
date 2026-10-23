package com.myProject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageRequest {
    @NotBlank(message = "Image URL is a required field")
    private String imageUrl;
}
