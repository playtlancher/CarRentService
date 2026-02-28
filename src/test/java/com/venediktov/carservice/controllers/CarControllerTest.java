package com.venediktov.carservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.services.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @MockBean
    private com.venediktov.carservice.services.CustomUserDetailsService userDetailsService;

    @MockBean
    private com.venediktov.carservice.repositories.UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCars_Success() throws Exception {
        Car car = new Car();
        car.setBrand("Audi");
        
        when(carService.findAll()).thenReturn(Arrays.asList(car));

        mockMvc.perform(get("/car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].brand").value("Audi"));
    }

    @Test
    void getCarById_Found() throws Exception {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("BMW");

        when(carService.findById(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("BMW"));
    }

    @Test
    void postCar_Success() throws Exception {
        Car car = new Car();
        car.setBrand("Toyota");

        when(carService.save(any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void updateCar_Success() throws Exception {
        Car car = new Car();
        car.setBrand("Updated Brand");

        when(carService.update(eq(1L), any(Car.class))).thenReturn(car);

        mockMvc.perform(put("/car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Updated Brand"));
    }

    @Test
    void deleteCar_Success() throws Exception {
        when(carService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/car/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
