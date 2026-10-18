package com.myProject.seeder;

import com.myProject.entity.User;
import com.myProject.enums.PaymentMethod;
import com.myProject.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.myProject.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class SeedAdmin {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct 
    public void seedAdmin() {
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User user = User.builder()
            .name("Amaan")
            .email("admin@ecommerce.com")
            .password(passwordEncoder.encode("admin123"))
            .role(Role.ROLE_ADMIN)
            .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
            .build();
            userRepository.save(user);
        }
    }
}
