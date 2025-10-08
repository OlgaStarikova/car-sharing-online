package com.example.carsharingonline.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingonline.dto.rental.CreateRentalRequestDto;
import com.example.carsharingonline.dto.rental.RentalDto;
import com.example.carsharingonline.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.service.rental.RentalService;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    private static MockMvc mockMvc;
    @Mock
    private RentalService rentalService;

    @Autowired
    private ObjectMapper objectMapper;

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
            connection.createStatement().execute("SET time_zone = '+03:00'");
            String insertCarSql = """
                        INSERT INTO cars (
                            id, model, brand, car_body_type, inventory, dayly_fee, is_deleted
                        )
                        VALUES (?, ?, ?, ?, ?, ?, FALSE)
                    """;

            try (PreparedStatement ps = connection.prepareStatement(insertCarSql)) {
                // Вставка автомобиля с доступным инвентарём
                ps.setLong(1, TestDataUtil.TEST_CAR_ID_AVAILABLE);
                ps.setString(2, TestDataUtil.TEST_CAR_MODEL);
                ps.setString(3, TestDataUtil.TEST_CAR_BRAND);
                ps.setString(4, TestDataUtil.TEST_CAR_BODY_TYPE_STRING);
                ps.setInt(5, TestDataUtil.TEST_CAR_INVENTORY_AVAILABLE);
                ps.setBigDecimal(6, TestDataUtil.TEST_CAR_DAILY_FEE);
                ps.executeUpdate();

                // Вставка автомобиля с недоступным инвентарём
                ps.setLong(1, TestDataUtil.TEST_CAR_ID_NOT_AVAILABLE);
                ps.setString(2, TestDataUtil.TEST_CAR_MODEL);
                ps.setString(3, TestDataUtil.TEST_CAR_BRAND);
                ps.setString(4, TestDataUtil.TEST_CAR_BODY_TYPE_STRING);
                ps.setInt(5, TestDataUtil.TEST_CAR_INVENTORY_NOT_AVAILABLE);
                ps.setBigDecimal(6, TestDataUtil.TEST_CAR_DAILY_FEE);
                ps.executeUpdate();
            }

            String sql = """
                        INSERT INTO rentals (
                            id, rental_date, return_date, actual_return_date, 
                            user_id, car_id, is_deleted
                        )
                        VALUES (
                            ?, NOW(), DATE_ADD(NOW(), INTERVAL 2 DAY), NULL, ?, ?, FALSE
                        )
                    """;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, TestDataUtil.TEST_RENTAL_ID);
                ps.setLong(2, TestDataUtil.TEST_USER_ID);
                ps.setLong(3, TestDataUtil.TEST_CAR_ID_AVAILABLE);

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

    @WithUserDetails("Admin@gmail.com")
    @Test
    @DisplayName("Test save method, valid result")
    void save_validRequestDto_ok() throws Exception {
        // Given
        CreateRentalRequestDto requestDto = TestDataUtil.getTestCreateRentalRequestDto();
        RentalDto expected = TestDataUtil.getTestRentalDto();
        User user = TestDataUtil.getTestAdminUser();
        when(rentalService.createRental(any(User.class), eq(requestDto))).thenReturn(expected);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // When
        MvcResult result = mockMvc.perform(
                        post("/public/rentals/")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(result1 -> {
                            if (result1.getResponse().getStatus() != 200) {
                                System.out.println("Error response: "
                                        + result1.getResponse().getContentAsString());
                            }
                        }
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        RentalDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalDto.class);
        assertNotNull(actual);

        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
        assertEquals(expected.carDto().model(), actual.carDto().model());
        assertEquals(expected.carDto().brand(), actual.carDto().brand());
        assertEquals(expected.carDto().carBodyType(), actual.carDto().carBodyType());
        assertEquals(expected.carDto().inventory(), actual.carDto().inventory());
        assertEquals(0, expected.carDto().daylyFee().compareTo(actual.carDto().daylyFee()));
    }

    @WithUserDetails("Admin@gmail.com")
    @Test
    @DisplayName("Find All rentals")
    public void findAllRentals_Admin_isActive_Success() throws Exception {
        //Given
        Boolean isActive = true;
        Long userId = TestDataUtil.TEST_USER_ID;
        MvcResult result = mockMvc.perform(
                        get("/public/rentals/")
                                .param("userId", String.valueOf(userId))
                                .param("isActive", String.valueOf(isActive))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        //When
        RentalDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), RentalDto[].class);
        //Then
        assertEquals(1, actual.length);
    }

    @WithUserDetails("Admin@gmail.com")
    @Test
    @DisplayName("Find rental by Id")
    public void findRentalById_ValidRequest_Success() throws Exception {
        //Given
        RentalDto expected = TestDataUtil.getTestRentalDto();
        Long testId = TestDataUtil.TEST_RENTAL_ID;
        when(rentalService.findById(testId)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                        get("/public/rentals/" + testId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        //When
        RentalDto actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), RentalDto.class);
        //Then
        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
    }

    @WithUserDetails("Admin@gmail.com")
    @Test
    @DisplayName("""
            Test save method, valid result
            """)
    public void update_validRequestDto_ok() throws Exception {
        ReturnRentalRequestDto requestDto = TestDataUtil.getTestReturnRentalRequestDto();
        RentalDto expected = TestDataUtil.getTestClosedRentalDto();
        Long testId = TestDataUtil.TEST_RENTAL_ID;
        when(rentalService.findById(testId)).thenReturn(expected);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/public/rentals/" + testId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        RentalDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                RentalDto.class);
        assertNotNull(actual);
        assertEquals(expected.rentalDate(), actual.rentalDate());
        assertEquals(expected.returnDate(), actual.returnDate());
        assertEquals(expected.actualReturnDate(), actual.actualReturnDate());
        assertEquals(expected.carDto().model(), actual.carDto().model());
        assertEquals(expected.carDto().brand(), actual.carDto().brand());
        assertEquals(expected.carDto().carBodyType(), actual.carDto().carBodyType());
        assertEquals(expected.carDto().inventory(), actual.carDto().inventory());
        assertEquals(0, expected.carDto().daylyFee().compareTo(actual.carDto().daylyFee()));
    }

    @SneakyThrows
    private static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            connection.createStatement().execute(
                    "DELETE FROM rentals"
            );
            connection.createStatement().execute(
                    "DELETE FROM cars"
            );
        }
    }
}
