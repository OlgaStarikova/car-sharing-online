package com.example.carsharingonline.controller;

import com.example.carsharingonline.dto.CreateRentalRequestDto;
import com.example.carsharingonline.dto.RentalDto;
import com.example.carsharingonline.dto.ReturnRentalRequestDto;
import com.example.carsharingonline.model.User;
import com.example.carsharingonline.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rental management", description = "Endpoints for managing rentals")
@RequiredArgsConstructor
@RestController
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/registered/rentals/")
    @Operation(summary = "Get a list of rentals",
            description = "Get a list of all available rentals."
                    + "Params(optional): page = page number, size = count of rentals in one page,"
                    + " namefield = field for sorting. Available for registered users.")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','MANAGER')")
    public List<RentalDto> getRentals(@RequestParam(required = false) Long userId,
                                      @RequestParam(required = false) Boolean isActive,
                                      @ParameterObject @PageableDefault Pageable pageable) {
        return rentalService.findByUserIdAndActive(userId, isActive, pageable);
    }

    @GetMapping("/registered/rentals/{rentalId}")
    @Operation(summary = "Get the rental by rentalId", description = "Get the rental by userId"
            + "Params: rentalId = Id of the rental. Available for registered users.")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','MANAGER')")
    public RentalDto getRentalById(@PathVariable Long rentalId) {
        return rentalService.findById(rentalId);
    }

    @PostMapping("/registered/rentals/")
    @Operation(summary = "Create a new rental", description = "Create a new rental. "
            + "Available for admins.")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','MANAGER')")
    public RentalDto createRental(Authentication authentication,
                                  @RequestBody @Valid CreateRentalRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return rentalService.createRental(user, requestDto);
    }

    @PutMapping("/registered/rentals/{rentalId}")
    @Operation(summary = "Update the rental", description = "Update the rental by Id."
            + "Params: id = Id of the rental. Available for admins.")
    @PreAuthorize("hasAnyAuthority('CUSTOMER','MANAGER')")
    public RentalDto updateRental(@PathVariable Long rentalId,
                                  @RequestBody @Valid ReturnRentalRequestDto requestDto) {
        return rentalService.closeRental(rentalId, requestDto);
    }

}
