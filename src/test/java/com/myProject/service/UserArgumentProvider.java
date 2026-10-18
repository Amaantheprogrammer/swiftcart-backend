package com.myProject.service;

import com.myProject.entity.User;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UserArgumentProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
            Arguments.of(User.builder().email("admin@ecommerce.com").password("admin123").build()),
            Arguments.of(User.builder().email("josephstarc@ecommerce.com").password("123456").build())
        );
    }
}
