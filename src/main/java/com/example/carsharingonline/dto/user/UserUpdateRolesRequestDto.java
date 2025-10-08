package com.example.carsharingonline.dto.user;

import java.util.Set;

public record UserUpdateRolesRequestDto(
        Set<String> roleNames
) {
}
