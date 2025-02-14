package com.example.carsharingonline.service.impl;

import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.exception.RegistrationException;
import com.example.carsharingonline.mapper.UserMapper;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.RoleRepository;
import com.example.carsharingonline.repository.UserRepository;
import com.example.carsharingonline.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RegistrationException("The user with email "
                    + requestDto.email()
                    + " is already registered");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRoles(Set.of(roleRepository.getByRole(Role.RoleName.USER)));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
