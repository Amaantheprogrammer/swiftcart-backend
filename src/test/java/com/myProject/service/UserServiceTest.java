package com.myProject.service;

import com.myProject.entity.User;
import com.myProject.repository.UserRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(strings = {
            "admin@ecommerce.com",
            "josephstarc1@gmail.com"
    })
    void testFindByEmail(String email) {
        assertTrue(userRepository.existsByEmail(email), "Failed for: " + email);
    }

    @ParameterizedTest
    @CsvSource({
            "2, 1, 1",
            "5, 3, 2"
    })
    void testAddition(int expected, int a, int b) {
        assertEquals(expected, a + b);
    }

    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    void testFindByEmail(User user) {
        assertTrue(userRepository.existsByEmail(user.getEmail()), "Failed for: " + user.getEmail());
    }
}
