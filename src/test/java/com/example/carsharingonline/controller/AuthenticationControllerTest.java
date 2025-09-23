package com.example.carsharingonline.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingonline.dto.UserLoginRequestDto;
import com.example.carsharingonline.dto.UserLoginResponseDto;
import com.example.carsharingonline.dto.UserRegistrationRequestDto;
import com.example.carsharingonline.dto.UserResponseDto;
import com.example.carsharingonline.security.AuthenticationService;
import com.example.carsharingonline.security.JwtUtil;
import com.example.carsharingonline.service.UserService;
import com.example.carsharingonline.utils.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        tearDown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            String insertUserSql = """
                        INSERT INTO users (
                            id, email, password, first_name, last_name, is_deleted
                        )
                        VALUES (?, ?, ?, ?, ?, FALSE)
                    """;

            try (PreparedStatement ps = connection.prepareStatement(insertUserSql)) {
                ps.setLong(1, TestDataUtil.TEST_REGISTERED_USER_ID);
                ps.setString(2, TestDataUtil.TEST_REGISTERED_USER_EMAIL);
                ps.setString(3, passwordEncoder.encode(TestDataUtil.TEST_USER_PASSWORD));
                ps.setString(4, TestDataUtil.TEST_USER_FIRST_NAME);
                ps.setString(5, TestDataUtil.TEST_USER_LAST_NAME);
                ps.executeUpdate();
            }

            String insertUserRoleSql = """
                        INSERT INTO users_roles (user_id, role_id)
                        VALUES (?, (SELECT id FROM roles WHERE role = 'CUSTOMER'))
                    """;

            try (PreparedStatement ps = connection.prepareStatement(insertUserRoleSql)) {
                ps.setLong(1, TestDataUtil.TEST_REGISTERED_USER_ID);
                ps.executeUpdate();
            }

        }
    }

    @AfterEach
    public void afterEach(
            @Autowired DataSource dataSource
    ) {
        tearDown(dataSource);
    }

    @Test
    @DisplayName("Register a new user with valid request")
    void register_validRequestDto_ok() throws Exception {
        // Given
        UserRegistrationRequestDto requestDto = TestDataUtil.getTestUserRegistrationRequestDto();
        UserResponseDto expected = TestDataUtil.getTestUserResponseDto();
        when(userService.register(any(UserRegistrationRequestDto.class))).thenReturn(expected);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        System.out.println("Request JSON: " + jsonRequest);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/register")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(result1 -> {
                    if (result1.getResponse().getStatus() != 200) {
                        System.out.println("Error response: "
                                + result1.getResponse().getContentAsString());
                    }
                }).andExpect(status().isOk())
                .andReturn();

        // Then
        UserResponseDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.email(), actual.email());
        assertEquals(expected.firstName(), actual.firstName());
        assertEquals(expected.lastName(), actual.lastName());
    }

    @Test
    @DisplayName("Login with valid credentials")
    void login_validRequestDto_ok() throws Exception {
        // Given
        UserLoginRequestDto requestDto = TestDataUtil.getTestUserLoginRequestDto();
        UserLoginResponseDto expected = TestDataUtil.getTestUserLoginResponseDto();
        when(jwtUtil.generateToken(any(String.class))).thenReturn(TestDataUtil.TEST_TOKEN);
        when(authenticationService.authenticate(any(UserLoginRequestDto.class)))
                .thenReturn(expected);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        System.out.println("Request JSON: " + jsonRequest);

        // When
        MvcResult result = mockMvc.perform(
                        post("/auth/login")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(result1 -> {
                    if (result1.getResponse().getStatus() != 200) {
                        System.out.println("Error response: " + result1.getResponse()
                                .getContentAsString());
                    }
                }).andExpect(status().isOk())
                .andReturn();

        // Then
        UserLoginResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserLoginResponseDto.class);
        assertNotNull(actual);
        assertEquals(expected.token(), actual.token());
    }

    @SneakyThrows
    private static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            connection.createStatement().execute("DELETE FROM users_roles WHERE "
                    + " user_id = " + TestDataUtil.TEST_REGISTERED_USER_ID);
            connection.createStatement().execute("DELETE FROM users WHERE "
                    + " id = " + TestDataUtil.TEST_REGISTERED_USER_ID);
            ;
        }
    }
}
