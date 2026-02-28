package com.venediktov.carservice.services;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.repositories.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setBrand("Tesla");
        car.setModel("Model S");
        car.setCityBased("Berlin");
    }

    @Test
    void findById_Found() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        
        Optional<Car> result = carService.findById(1L);
        
        assertTrue(result.isPresent());
        assertEquals("Tesla", result.get().getBrand());
    }

    @Test
    void findAll_ReturnsList() {
        when(carRepository.findAll()).thenReturn(Arrays.asList(car));
        
        List<Car> result = carService.findAll();
        
        assertEquals(1, result.size());
        assertEquals("Tesla", result.get(0).getBrand());
    }

    @Test
    void save_Success() {
        when(carRepository.save(car)).thenReturn(car);
        
        Car result = carService.save(car);
        
        assertNotNull(result);
        assertEquals("Model S", result.getModel());
        verify(carRepository).save(car);
    }

    @Test
    void findByCity_ReturnsFilteredList() {
        when(carRepository.findByCityBased("Berlin")).thenReturn(Arrays.asList(car));
        
        List<Car> result = carService.findByCity("Berlin");
        
        assertEquals(1, result.size());
        assertEquals("Berlin", result.get(0).getCityBased());
    }

    @Test
    void delete_Success() {
        boolean result = carService.delete(1L);
        
        assertTrue(result);
        verify(carRepository).deleteById(1L);
    }
}
