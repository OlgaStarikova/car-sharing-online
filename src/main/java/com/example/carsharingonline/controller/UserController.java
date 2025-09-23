package com.example.carsharingonline.controller;

import com.example.carsharingonline.dto.UserProfileRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.dto.UserUpdateRolesRequestDto;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User profile management", description = "Endpoints for user's profile")
@RequiredArgsConstructor
@RestController
@RequestMapping("registered/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get a user's profile", description = "Get a user's profile "
            + "for authentication user ")
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public UserResponseDto getProfile(Authentication authentication,
                                      @ParameterObject @PageableDefault Pageable pageable
    ) {
        User user = (User) authentication.getPrincipal();
        return userService.getProfile(user, pageable);
    }

    @PutMapping("/me")
    public UserResponseDto updateProfile(Authentication authentication,
                                         @RequestBody UserProfileRequestDto request
    ) {
        User user = (User) authentication.getPrincipal();
        return userService.updateProfile(user.getId(), request);
    }

    @PatchMapping("/me")
    public UserResponseDto patchProfile(
            Authentication authentication,
            @RequestBody UserProfileRequestDto request
    ) {
        User user = (User) authentication.getPrincipal();
        return userService.patchProfile(user.getId(), request);
    }

    //PUT: /users/{id}/role - update user role
    @Operation(summary = "UPDATE a user's ROLES", description = "Update a user`s roles "
            + "for authentication user ")
    @PutMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public UserResponseDto updateRole(@PathVariable Long userId,
                                      @RequestBody UserUpdateRolesRequestDto request
    ) {
        return userService.updateUserRoles(userId, request);
    }
}
