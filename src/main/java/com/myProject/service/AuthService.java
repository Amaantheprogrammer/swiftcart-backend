package com.myProject.service;

import com.myProject.dto.response.*;
import com.myProject.dto.request.*;
import com.myProject.entity.User;
import com.myProject.enums.Role;
import com.myProject.exception.InvalidRefreshTokenException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myProject.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse signIn(SignInRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = refreshTokenService.createRefreshToken(request.getEmail());

        addRefreshTokenCookie(response, refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "usersById", allEntries = true),
            @CacheEvict(value = "usersByEmail", allEntries = true)
    })
    public void signUp(SignupRequest request) {
        if (request.getRole() == Role.ROLE_ADMIN) {
                throw new IllegalArgumentException("Cannot sign-up as admin");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .paymentMethod(request.getPaymentMethod())
                .role(request.getRole())
                .address(request.getAddress())
                .build();
        userRepository.save(user);
    }

    @Transactional
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refreshToken");
        if (refreshToken != null) {
            refreshTokenService.deleteRefreshToken(refreshToken);
        }
        clearRefreshTokenCookie(response);
    }

    @Transactional
    public AuthResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String currentRefreshToken = extractCookie(request, "refreshToken");
        if (currentRefreshToken == null) {
            throw new InvalidRefreshTokenException("Refresh token missing"); // Or custom exception
        }

        String email = refreshTokenService.verifyAndGetEmail(currentRefreshToken);
        if (email == null) {
            throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        }

        refreshTokenService.deleteRefreshToken(currentRefreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String newAccessToken = jwtService.generateToken(email, user.getRole().name());
        String newRefreshToken = refreshTokenService.createRefreshToken(email);

        addRefreshTokenCookie(response, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    // Private methods
    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // True only when in production
                .path("/")
                .maxAge(Duration.ofMillis(refreshExpiration))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
