package com.example.carsharingonline.service.rental;

import com.example.carsharingonline.dto.rental.CreateRentalRequestDto;
import com.example.carsharingonline.dto.rental.RentalDto;
import com.example.carsharingonline.dto.rental.ReturnRentalRequestDto;
import com.example.carsharingonline.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalDto createRental(User user, CreateRentalRequestDto requestDto);

    RentalDto findById(Long id);

    RentalDto closeRental(Long id, ReturnRentalRequestDto requestDto);

    List<RentalDto> findByUserIdAndActive(Long userId, Boolean isActive, Pageable pageable);

    void checkOverdueRentals();
}

