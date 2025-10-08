package com.example.carsharingonline.service.user;

import com.example.carsharingonline.dto.user.UserProfileRequestDto;
import com.example.carsharingonline.dto.user.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.user.UserResponseDto;
import com.example.carsharingonline.dto.user.UserUpdateRolesRequestDto;
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
