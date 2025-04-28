package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.model.User;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);

    User findUserById(Long userId);
}
