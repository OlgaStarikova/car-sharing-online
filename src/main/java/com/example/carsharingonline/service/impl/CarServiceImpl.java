package com.example.carsharingonline.service.impl;

import com.example.carsharingonline.dto.CarDto;
import com.example.carsharingonline.dto.CreateCarRequestDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.mapper.CarMapper;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.repository.CarRepository;
import com.example.carsharingonline.service.CarService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarDto save(CreateCarRequestDto requestDto) {
        return Optional.ofNullable(requestDto)
                .map(carMapper::toModel)
                .map(carRepository::save)
                .map(carMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Input parameters can't be null"));
    }

    @Override
    public List<CarDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarDto findById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + id));
        return carMapper.toDto(car);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public CarDto updateCar(Long id, CreateCarRequestDto requestDto) {
        Car car = carMapper.toModel(requestDto);
        car.setId(id);
        carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find car by id " + id));
        return carMapper.toDto(carRepository.save(car));
    }
}
