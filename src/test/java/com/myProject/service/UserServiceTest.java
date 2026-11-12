package com.myProject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.swiftcart.enums.PaymentMethod;
import com.swiftcart.enums.Role;
import com.swiftcart.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.swiftcart.dto.request.UpdateUserRequest;
import com.swiftcart.dto.response.UserResponse;
import com.swiftcart.entity.User;
import com.swiftcart.repository.UserRepository;
import com.swiftcart.util.CurrentUserUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserUtil currentUserUtil;
    @Mock
    private ModelMapper modelMapper;
    
    @Test
    void getUserByIdTest() {
        // Mock user
        User existingUser = User.builder()
                .id(1L)
                .name("Samuel Johnson")
                .email("samuel.johnson@gmail.com")
                .password("123456")
                .role(Role.ROLE_USER)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .address("Toronto, 342 Doritos street")
                .build();
        // Expected response
        UserResponse expectedUserResponse = UserResponse.builder()
                .id(1L)
                .name("Samuel Johnson")
                .email("samuel.johnson@gmail.com")
                .role(Role.ROLE_USER)
                .build();
        
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(expectedUserResponse);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserResponse actualUserResponse = userService.getUserById(1L);

        // Assertions
        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName());
        assertEquals(expectedUserResponse.getEmail(), actualUserResponse.getEmail());
    }

    @Test
    void getUserByEmailTest() {
        // Mock user
        User existingUser = User.builder()
                .id(1L)
                .name("Samuel Johnson")
                .email("samuel.johnson@gmail.com")
                .password("123456")
                .role(Role.ROLE_USER)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .address("Toronto, 342 Doritos street")
                .build();
        // Expected response
        UserResponse expectedUserResponse = UserResponse.builder()
                .id(1L)
                .name("Samuel Johnson")
                .email("samuel.johnson@gmail.com")
                .role(Role.ROLE_USER)
                .build();
        
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(expectedUserResponse);
        when(userRepository.findByEmail("samuel.johnson@gmail.com")).thenReturn(Optional.of(existingUser));

        UserResponse actualUserResponse = userService.getUserByEmail("samuel.johnson@gmail.com");

        // Assertions
        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName());
        assertEquals(expectedUserResponse.getEmail(), actualUserResponse.getEmail());
    }
    
    @Test
    void updateMyUserTest() {
        // Mock user and request
        User existingUser = User.builder()
                .id(1L)
                .name("Samuel Johnson")
                .email("samuel.johnson@gmail.com")
                .password("123456")
                .role(Role.ROLE_USER)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .address("Toronto, 342 Doritos street")
                .build();
        UpdateUserRequest request = new UpdateUserRequest(
                "Sam Wilson",
                "sam.wilson@gmail.com",
                PaymentMethod.CASH_ON_DELIVERY
        );
        // Expected response
        UserResponse expectedUserResponse = UserResponse.builder()
                .id(1L)
                .name("Sam Wilson")
                .email("sam.wilson@gmail.com")
                .role(Role.ROLE_USER)
                .build();

        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(expectedUserResponse);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(currentUserUtil.getCurrentUser()).thenReturn(existingUser);

        // Actual response
        UserResponse actualUserResponse = userService.updateMyUser(request);

        // Assertions
        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse.getName(), actualUserResponse.getName());
    }
}
