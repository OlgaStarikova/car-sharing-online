package com.example.carsharingonline.service.user.impl;

import com.example.carsharingonline.dto.user.UserProfileRequestDto;
import com.example.carsharingonline.dto.user.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.user.UserResponseDto;
import com.example.carsharingonline.dto.user.UserUpdateRolesRequestDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.exception.RegistrationException;
import com.example.carsharingonline.mapper.UserMapper;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.RoleRepository;
import com.example.carsharingonline.repository.UserRepository;
import com.example.carsharingonline.service.user.UserService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        user.setRoles(Set.of(roleRepository.getByRole(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                        + Role.RoleName.CUSTOMER))));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User was not found for Id = " + userId + ""));
    }

    @Override
    public UserResponseDto getProfile(User user, Pageable pageable) {
        return userMapper.toDto(user);
    }

    public UserResponseDto updateProfile(Long userId, UserProfileRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User was not found for Id = "
                        + userId + ""));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDto patchProfile(Long userId, UserProfileRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User was not found for Id = "
                        + userId + ""));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDto updateUserRoles(Long userId, UserUpdateRolesRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User was not found for Id = "
                        + userId + ""));

        Set<Role> setRoles = request.roleNames().stream()
                .map(roleName -> roleRepository.getByRole(Role.RoleName.valueOf(roleName))
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: "
                                + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(setRoles);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
