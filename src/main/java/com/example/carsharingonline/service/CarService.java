package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.CarDto;
import com.example.carsharingonline.dto.CreateCarRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarDto save(CreateCarRequestDto requestDto);

    List<CarDto> findAll(Pageable pageable);

    CarDto findById(Long id);

    void deleteCar(Long id);

    CarDto updateCar(Long id, CreateCarRequestDto requestDto);
}
