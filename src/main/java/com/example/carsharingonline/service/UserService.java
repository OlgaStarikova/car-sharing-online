package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.UserProfileRequestDto;
import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.dto.UserUpdateRolesRequestDto;
import com.example.carsharingonline.model.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    User findUserById(Long userId);

    UserResponseDto getProfile(User user, Pageable pageable);

    UserResponseDto updateProfile(Long userId, UserProfileRequestDto request);

    UserResponseDto patchProfile(Long userId, UserProfileRequestDto request);

    public UserResponseDto updateUserRoles(Long userId, UserUpdateRolesRequestDto request);
}
