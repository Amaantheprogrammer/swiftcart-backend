package com.myProject.dto.request;

import com.myProject.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Name is a required field")
    private String name;

    @NotBlank(message = "Email is a required field")
    @Email
    private String email;

    @NotBlank(message = "Password is a required field")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;

    @NotNull(message = "Payment method is a required field")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Role is a required field")
    private Role role;

    @NotBlank(message = "Address is a required field")
    private String address;
}
