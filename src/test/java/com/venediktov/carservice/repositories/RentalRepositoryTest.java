package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.Rental;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    void findByUserId() {
        Rental rental = new Rental();
        rental.setUserId(99L);
        rental.setCarId(1L);
        rental.setStartDate(LocalDate.now());
        rental.setEndDate(LocalDate.now().plusDays(1));
        rentalRepository.save(rental);

        List<Rental> rentals = rentalRepository.findByUserId(99L);
        assertFalse(rentals.isEmpty());
        assertEquals(99L, rentals.get(0).getUserId());
    }

    @Test
    void findByCarId() {
        Rental rental = new Rental();
        rental.setCarId(5L);
        rental.setUserId(1L);
        rentalRepository.save(rental);

        List<Rental> rentals = rentalRepository.findByCarId(5L);
        assertFalse(rentals.isEmpty());
        assertEquals(5L, rentals.get(0).getCarId());
    }
}
