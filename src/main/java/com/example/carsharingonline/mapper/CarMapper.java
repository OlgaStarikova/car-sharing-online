package com.example.carsharingonline.mapper;

import com.example.carsharingonline.config.MapperConfig;
import com.example.carsharingonline.dto.CarDto;
import com.example.carsharingonline.dto.CreateCarRequestDto;
import com.example.carsharingonline.model.Car;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarDto toDto(Car car);

    Car toModel(CreateCarRequestDto requestDto);

    @Mapping(source = "car.id", target = "carId")
    List<CarDto> toDtos(List<Car> cars);
}
