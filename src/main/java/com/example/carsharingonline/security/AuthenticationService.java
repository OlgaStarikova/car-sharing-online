package com.example.carsharingonline.security;

import com.example.carsharingonline.dto.UserLoginRequestDto;
import com.example.carsharingonline.dto.UserLoginResponseDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        UserLoginRequestDto requestDto1 = requestDto;
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        String token = jwtUtil.generateToken(requestDto.email());
        return new UserLoginResponseDto(token);
    }

    public Optional<Long> getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }

    public static boolean hasRole(String role) {
        return SecurityUtil.hasRole(role);
    }
}
