package com.myProject.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponse implements Serializable {
    private Long id;
    private String imageUrl;
}
