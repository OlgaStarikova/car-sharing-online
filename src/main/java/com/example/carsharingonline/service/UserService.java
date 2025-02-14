package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto);
}
