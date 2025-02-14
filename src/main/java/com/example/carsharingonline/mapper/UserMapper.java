package com.example.carsharingonline.mapper;

import com.example.carsharingonline.config.MapperConfig;
import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
