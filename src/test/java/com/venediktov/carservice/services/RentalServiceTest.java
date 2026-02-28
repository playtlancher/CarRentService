package com.venediktov.carservice.services;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.model.Rental;
import com.venediktov.carservice.model.User;
import com.venediktov.carservice.repositories.CarRepository;
import com.venediktov.carservice.repositories.RentalRepository;
import com.venediktov.carservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalService rentalService;

    private Car car;
    private User user;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setStatus(Car.CarStatus.AVAILABLE);
        car.setDailyPrice(BigDecimal.valueOf(100));

        user = new User();
        user.setId(10L);
        user.setUsername("testuser");
    }

    @Test
    void rentCar_Success() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(3);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental result = rentalService.rentCar(1L, "testuser", startDate, endDate);

        assertNotNull(result);
        assertEquals(user.getId(), result.getUserId());
        assertEquals(car.getId(), result.getCarId());
        assertEquals(BigDecimal.valueOf(300), result.getTotalPrice());
        assertEquals(Rental.RentalStatus.ACTIVE, result.getStatus());
        assertEquals(Car.CarStatus.RENTED, car.getStatus());

        verify(carRepository).save(car);
        verify(rentalRepository).save(any(Rental.class));
    }

    @Test
    void rentCar_StartDateInPast_ThrowsException() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            rentalService.rentCar(1L, "testuser", startDate, endDate)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Rental start date cannot be in the past", exception.getReason());
    }

    @Test
    void rentCar_EndDateBeforeStartDate_ThrowsException() {
        LocalDate startDate = LocalDate.now().plusDays(2);
        LocalDate endDate = LocalDate.now().plusDays(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            rentalService.rentCar(1L, "testuser", startDate, endDate)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Return date cannot be before start date", exception.getReason());
    }

    @Test
    void rentCar_CarAlreadyRented_ThrowsException() {
        car.setStatus(Car.CarStatus.RENTED);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(1);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            rentalService.rentCar(1L, "testuser", startDate, endDate)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Car is already rented", exception.getReason());
    }

    @Test
    void rentCar_CarNotFound_ThrowsException() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> 
            rentalService.rentCar(1L, "testuser", LocalDate.now(), LocalDate.now().plusDays(1))
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }
    @Test
    void rentCar_DefaultPriceIfMissing() {
        car.setDailyPrice(null);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(1);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental result = rentalService.rentCar(1L, "testuser", startDate, endDate);

        assertEquals(BigDecimal.valueOf(100), result.getTotalPrice()); 
    }

    @Test
    void rentCar_MinOneDayIfSameDate() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate; 

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental result = rentalService.rentCar(1L, "testuser", startDate, endDate);

        assertEquals(BigDecimal.valueOf(100), result.getTotalPrice()); 
    }
}
