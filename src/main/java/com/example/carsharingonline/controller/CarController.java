package com.example.carsharingonline.controller;

import com.example.carsharingonline.dto.CarDto;
import com.example.carsharingonline.dto.CreateCarRequestDto;
import com.example.carsharingonline.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Car management", description = "Endpoints for managing cars")
@RequiredArgsConstructor
@RestController
public class CarController {
    private final CarService carService;

    @GetMapping("/cars")
    @Operation(summary = "Get a list of cars", description = "Get a list of all available cars."
            + "Params(optional): page = page number, size = count of cars in one page,"
            + " namefield = field for sorting. Available for all.")
    public List<CarDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/cars/{id}")
    @Operation(summary = "Get the car by Id", description = "Get the car by Id"
            + "Params: id = Id of the car. Available for all.")
   public CarDto getCarById(@PathVariable Long id) {
        return carService.findById(id);
    }

    @PostMapping("/admin/cars")
    @Operation(summary = "Create a new car", description = "Create a new car. "
            + "Available for admins.")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CarDto createCar(@RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.save(requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/cars/{id}")
    @Operation(summary = "Delete the car", description = "Delete the car by Id."
            + "Params: id = Id of the car. Available for admins.")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCarById(@PathVariable Long id) {
        carService.deleteCar(id);
    }

    @PutMapping("/admin/cars/{id}")
    @Operation(summary = "Update the car", description = "Update the car by Id."
            + "Params: id = Id of the car. Available for admins.")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CarDto updateCar(@PathVariable Long id,
                            @RequestBody @Valid CreateCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }
}
