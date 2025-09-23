package com.example.carsharingonline.mapper;

import com.example.carsharingonline.config.MapperConfig;
import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    @Mapping(source = "roles", target = "roleNames", qualifiedByName = "setRoleNamesDto")
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);

    @Named("setRoleNamesDto")
    default Set<String> getRoleNameForDto(Set<Role> roles) {
        return roles.stream()
                .map(r -> r.getRole().toString())
                .collect(Collectors.toSet());
    }

}
