package com.example.carsharingonline.mapper;

import com.example.carsharingonline.config.MapperConfig;
import com.example.carsharingonline.dto.CreateRentalRequestDto;
import com.example.carsharingonline.dto.RentalDto;
import com.example.carsharingonline.dto.ReturnRentalRequestDto;
import com.example.carsharingonline.model.Rental;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = {MapperUtils.class},
        imports = {
                MapperUtils.class
        })
public interface RentalMapper {

    @Mapping(source = "car.id", target = "carDto", qualifiedByName =
            "mapCarIdToCarDto")
    RentalDto toDto(Rental rental);

    Rental toModelFromCreateDto(CreateRentalRequestDto requestDto);

    Rental toModelFromReturnDto(ReturnRentalRequestDto requestDto);

    @Mapping(source = "rental.id", target = "rentalId")
    List<RentalDto> toDtos(List<Rental> rentals);

}
