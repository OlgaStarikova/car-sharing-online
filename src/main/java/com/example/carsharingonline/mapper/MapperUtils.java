package com.example.carsharingonline.mapper;

import com.example.carsharingonline.dto.car.CarDto;
import com.example.carsharingonline.exception.EntityNotFoundException;
import com.example.carsharingonline.model.Car;
import com.example.carsharingonline.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Named("MapperUtils")
@Component
@RequiredArgsConstructor
public class MapperUtils {
    private final CarRepository carRepository;

    @Named("mapCarIdToCarDto")
    public CarDto mapCarIdToCarDto(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found car by carId = " + carId)
                );
        return new CarDto(
                car.getModel(),
                car.getBrand(),
                car.getCarBodyType().name(),
                car.getInventory(),
                car.getDaylyFee()
        );
    }
}
