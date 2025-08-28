package com.example.carsharingonline.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.carsharingonline.dto.CarDto;
import com.example.carsharingonline.dto.CreateCarRequestDto;
import com.example.carsharingonline.service.CarService;
import com.example.carsharingonline.utils.TestDataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    private static MockMvc mockMvc;
    @Mock
    private CarService carService;

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
            String insertCarSql = """
                        INSERT INTO cars (
                            id, model, brand, car_body_type, inventory, dayly_fee, is_deleted
                        )
                        VALUES (?, ?, ?, ?, ?, ?, FALSE)
                    """;

            try (PreparedStatement ps = connection.prepareStatement(insertCarSql)) {

                ps.setLong(1, TestDataUtil.TEST_CAR_ID_AVAILABLE);
                ps.setString(2, TestDataUtil.TEST_CAR_MODEL);
                ps.setString(3, TestDataUtil.TEST_CAR_BRAND);
                ps.setString(4, TestDataUtil.TEST_CAR_BODY_TYPE_STRING);
                ps.setInt(5, TestDataUtil.TEST_CAR_INVENTORY_AVAILABLE);
                ps.setBigDecimal(6, TestDataUtil.TEST_CAR_DAILY_FEE);
                ps.executeUpdate();

                ps.setLong(1, TestDataUtil.TEST_CAR_ID_NOT_AVAILABLE);
                ps.setString(2, TestDataUtil.TEST_CAR_MODEL);
                ps.setString(3, TestDataUtil.TEST_CAR_BRAND);
                ps.setString(4, TestDataUtil.TEST_CAR_BODY_TYPE_STRING);
                ps.setInt(5, TestDataUtil.TEST_CAR_INVENTORY_NOT_AVAILABLE);
                ps.setBigDecimal(6, TestDataUtil.TEST_CAR_DAILY_FEE);
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

    @WithMockUser(username = "Admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("""
            Test save method, valid result
            """)
    public void save_validRequestDto_ok() throws Exception {
        CreateCarRequestDto requestDto = TestDataUtil.getTestCreateCarRequestDtoNew();
        CarDto expected = TestDataUtil.getTestCarDtoAvailable();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/admin/cars")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        CarDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.model(), actual.model());
        Assertions.assertEquals(expected.brand(), actual.brand());
        Assertions.assertEquals(expected.carBodyType(), actual.carBodyType());
        Assertions.assertEquals(expected.inventory(), actual.inventory());
        Assertions.assertEquals(0, expected.daylyFee().compareTo(actual.daylyFee()));
        //Assertions.assertEquals(expected, actual);
    }

    @WithMockUser(username = "User", authorities = {"USER"})
    @Test
    @DisplayName("Find All cars")
    public void findAllCars_ValidRequest_Success() throws Exception {
        //Given
        MvcResult result = mockMvc.perform(
                        get("/cars")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        //When
        CarDto[] actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), CarDto[].class);
        //Then
        Assertions.assertEquals(2, actual.length);
    }

    @WithMockUser(username = "User", authorities = {"USER"})
    @Test
    @DisplayName("Find car by Id")
    public void findCarById_ValidRequest_Success() throws Exception {
        //Given
        CarDto expected = TestDataUtil.getTestCarDtoAvailable();
        Long testId = TestDataUtil.TEST_CAR_ID_AVAILABLE;
        when(carService.findById(testId)).thenReturn(expected);
        MvcResult result = mockMvc.perform(
                        get("/cars/" + testId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();
        //When
        CarDto actual = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), CarDto.class);
        //Then
        Assertions.assertEquals(expected.model(), actual.model());
        Assertions.assertEquals(expected.brand(), actual.brand());
        Assertions.assertEquals(expected.carBodyType(), actual.carBodyType());
        Assertions.assertEquals(expected.inventory(), actual.inventory());
        Assertions.assertEquals(0, expected.daylyFee().compareTo(actual.daylyFee()));
    }

    @WithMockUser(username = "Admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("Delete existing car")
    public void delete_anyRequest_Success() throws Exception {
        Long testId = TestDataUtil.TEST_CAR_ID_AVAILABLE;
        MvcResult result = mockMvc.perform(
                        delete("/admin/cars/" + testId)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "Admin", authorities = {"ADMIN"})
    @Test
    @DisplayName("""
            Test save method, valid result
            """)
    public void update_validRequestDto_ok() throws Exception {
        CreateCarRequestDto requestDto = TestDataUtil.getTestCreateCarRequestDto();
        CarDto expected = TestDataUtil.getTestCarDtoAvailable();
        Long testId = TestDataUtil.TEST_CAR_ID_AVAILABLE;
        when(carService.findById(testId)).thenReturn(expected);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        put("/admin/cars/" + testId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CarDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.model(), actual.model());
        Assertions.assertEquals(expected.brand(), actual.brand());
        Assertions.assertEquals(expected.carBodyType(), actual.carBodyType());
        Assertions.assertEquals(expected.inventory(), actual.inventory());
        Assertions.assertEquals(0, expected.daylyFee().compareTo(actual.daylyFee()));

    }

    @SneakyThrows
    private static void tearDown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            connection.createStatement().execute(
                    "DELETE FROM cars"
            );
        }
    }
}
