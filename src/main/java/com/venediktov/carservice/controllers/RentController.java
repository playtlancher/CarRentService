package com.venediktov.carservice.controllers;

import com.venediktov.carservice.dto.CarRentView;
import com.venediktov.carservice.model.Rental;
import com.venediktov.carservice.services.RentalService;
import com.venediktov.carservice.dto.RentCarRequest;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentController {
    private final RentalService rentalService;

    public RentController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rent")
    public com.venediktov.carservice.model.Rental rentCar(@RequestBody RentCarRequest request, java.security.Principal principal) {
        return rentalService.rentCar(
                request.getCarId(),
                principal.getName(),
                request.getStartDate(),
                request.getEndDate()
        );
    }

    @GetMapping("/history")
    public List<com.venediktov.carservice.model.Rental> getHistory(java.security.Principal principal) {
         return rentalService.getRentsByUsername(principal.getName());
    }

    @GetMapping("/car/{carId}")
    public List<com.venediktov.carservice.model.Rental> getCarRentals(@PathVariable Long carId) {
        return rentalService.getOccupiedDates(carId);
    }
}
