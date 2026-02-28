package com.venediktov.carservice.controllers;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.services.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {
    private final CarService carService;

    public CarController(CarService repo) {
        this.carService = repo;
    }

    @GetMapping("/{id}")
    public Car get(@PathVariable Long id) {
        return this.carService.findById(id).orElse(null);
    }

    @GetMapping()
    public List<Car> getAll(@RequestParam(required = false) String city) {
        if (city != null && !city.isEmpty()) {
            return this.carService.findByCity(city);
        } else {
            return this.carService.findAll();
        }
    }

    @PostMapping()
    public Car postCar(@RequestBody Car car) {
        return this.carService.save(car);
    }

    @PutMapping("/{id}")
    public Car update(@PathVariable Long id, @RequestBody Car car) {
        car.setId(id);
        return this.carService.update(id, car);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        this.carService.delete(id);
        return true;
    }
}
