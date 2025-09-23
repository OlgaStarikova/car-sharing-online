package com.example.carsharingonline.dto;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roleNames
) {
}
