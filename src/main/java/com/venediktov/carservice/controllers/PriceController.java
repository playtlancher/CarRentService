package com.venediktov.carservice.controllers;

import com.venediktov.carservice.services.CarService;
import com.venediktov.carservice.services.PriceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/price")
public class PriceController {
    
    private final PriceService priceService;
    private final CarService carService;

    public PriceController(PriceService priceService, CarService carService) {
        this.priceService = priceService;
        this.carService = carService;
    }

    @GetMapping(value = "/calculate/{id}")
    public java.math.BigDecimal getPrice(@PathVariable Long id) {
        com.venediktov.carservice.model.Car car = carService.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        return this.priceService.calculateDailyPrice(car);
    }
}
