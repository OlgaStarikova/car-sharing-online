package com.example.carsharingonline.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.carsharingonline.dto.user.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.user.UserResponseDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.exception.RegistrationException;
import com.example.carsharingonline.mapper.UserMapper;
import com.example.carsharingonline.model.Role;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.repository.RoleRepository;
import com.example.carsharingonline.repository.UserRepository;
import com.example.carsharingonline.service.user.impl.UserServiceImpl;
import com.example.carsharingonline.utils.TestDataUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_ValidInput_ReturnsUserResponseDto() {
        UserRegistrationRequestDto requestDto = TestDataUtil.getTestUserRegistrationRequestDto();
        User user = TestDataUtil.getTestUser();
        UserResponseDto responseDto = TestDataUtil.getTestUserResponseDto();
        Role userRole = mock(Role.class);
        //when(userRole.getRole()).thenReturn(Role.RoleName.USER);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");
        when(roleRepository.getByRole(Role.RoleName.CUSTOMER))
                .thenReturn(Optional.ofNullable(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.register(requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userMapper).toModel(requestDto);
        verify(passwordEncoder).encode(requestDto.password());
        verify(roleRepository).getByRole(Role.RoleName.CUSTOMER);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void register_EmailAlreadyExists_ThrowsRegistrationException() {
        UserRegistrationRequestDto requestDto = TestDataUtil.getTestUserRegistrationRequestDto();
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThrows(RegistrationException.class,
                () -> userService.register(requestDto),
                "The user with email " + requestDto.email() + " is already registered");
        verify(userRepository).existsByEmail(requestDto.email());
        verify(userMapper, never()).toModel(any());
        verify(passwordEncoder, never()).encode(any());
        verify(roleRepository, never()).getByRole(any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findUserById_ValidId_ReturnsUser() {
        Long userId = TestDataUtil.TEST_USER_ID;
        User user = TestDataUtil.getTestUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserById_NonExistingId_ThrowsEntityNotFoundException() {
        Long userId = TestDataUtil.TEST_USER_ID;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(userId),
                "User was not found for Id = " + userId);
        verify(userRepository).findById(userId);
    }
}
