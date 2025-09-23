package com.example.carsharingonline.dto;

import java.util.Set;

public record UserUpdateRolesRequestDto(
        Set<String> roleNames
) {
}
