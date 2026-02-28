package com.venediktov.carservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.venediktov.carservice.dto.RentCarRequest;
import com.venediktov.carservice.model.Rental;
import com.venediktov.carservice.services.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class RentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private com.venediktov.carservice.services.CustomUserDetailsService userDetailsService;

    @MockBean
    private com.venediktov.carservice.repositories.UserRepository userRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "testuser")
    void rentCar_Success() throws Exception {
        RentCarRequest request = new RentCarRequest();
        request.setCarId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));

        Rental rental = new Rental();
        rental.setId(100L);
        rental.setCarId(1L);
        rental.setStatus(Rental.RentalStatus.ACTIVE);

        when(rentalService.rentCar(eq(1L), eq("testuser"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rental);

        mockMvc.perform(post("/rentals/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rentCar_InvalidDates_ReturnsBadRequest() throws Exception {
        RentCarRequest request = new RentCarRequest();
        request.setCarId(1L);
        request.setStartDate(LocalDate.now().minusDays(5));
        request.setEndDate(LocalDate.now().plusDays(1));

        when(rentalService.rentCar(anyLong(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rental start date cannot be in the past"));

        mockMvc.perform(post("/rentals/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "testuser"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCarRentals_Success() throws Exception {
        when(rentalService.getOccupiedDates(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rentals/car/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
