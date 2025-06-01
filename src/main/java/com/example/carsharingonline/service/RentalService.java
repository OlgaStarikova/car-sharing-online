package com.example.carsharingonline.service;

import com.example.carsharingonline.dto.CreateRentalRequestDto;
import com.example.carsharingonline.dto.RentalDto;
import com.example.carsharingonline.dto.ReturnRentalRequestDto;
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

