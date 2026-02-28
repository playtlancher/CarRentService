package com.venediktov.carservice.services;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.model.Rental;
import com.venediktov.carservice.model.User;
import com.venediktov.carservice.repositories.CarRepository;
import com.venediktov.carservice.repositories.RentalRepository;
import com.venediktov.carservice.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, CarRepository carRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Rental rentCar(Long carId, String username, LocalDate rentalDate, LocalDate returnDate) {
        if (rentalDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rental start date cannot be in the past");
        }
        if (returnDate.isBefore(rentalDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return date cannot be before start date");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));
        
        if (car.getStatus() == Car.CarStatus.RENTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Car is already rented");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Rental rental = new Rental();
        rental.setCarId(carId);
        rental.setUserId(user.getId());
        rental.setStartDate(rentalDate);
        rental.setEndDate(returnDate);
        rental.setStatus(Rental.RentalStatus.ACTIVE);
        
        long days = java.time.temporal.ChronoUnit.DAYS.between(rentalDate, returnDate);
        if (days < 1) days = 1; 
        

        BigDecimal pricePerDay = car.getDailyPrice();
        if (pricePerDay == null) pricePerDay = BigDecimal.valueOf(100); 
        
        BigDecimal totalPrice = pricePerDay.multiply(BigDecimal.valueOf(days));
        rental.setTotalPrice(totalPrice);
        
        car.setStatus(Car.CarStatus.RENTED);
        carRepository.save(car);
        
        return rentalRepository.save(rental);
    }
    
    public List<Rental> getRentsByUserId(Long userId) {
        return rentalRepository.findByUserId(userId);
    }

    public List<Rental> getRentsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return rentalRepository.findByUserId(user.getId());
    }

    public List<Rental> getOccupiedDates(Long carId) {
        return rentalRepository.findByCarId(carId);
    }
}
