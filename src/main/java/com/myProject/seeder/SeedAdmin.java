package com.myProject.seeder;

import com.myProject.entity.User;
import com.myProject.enums.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.myProject.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class SeedAdmin {

    @Value("${admin.name}")
    private String adminName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct 
    public void seedAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User user = User.builder()
            .name(adminName)
            .email(adminEmail)
            .password(passwordEncoder.encode(adminPassword))
            .role(Role.ROLE_ADMIN)
            .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
            .build();
            userRepository.save(user);
        }
    }
}
