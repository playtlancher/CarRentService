package com.venediktov.carservice.repositories;

import com.venediktov.carservice.model.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    void saveAndFindById() {
        Car car = new Car();
        car.setBrand("Porsche");
        car.setModel("911");
        car.setVinCode("PORSCHE123456");
        car.setCityBased("Stuttgart");

        Car saved = carRepository.save(car);
        assertNotNull(saved.getId());

        Car found = carRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Porsche", found.getBrand());
    }

    @Test
    void findByCityBased() {
        Car car1 = new Car();
        car1.setCityBased("Berlin");
        carRepository.save(car1);

        Car car2 = new Car();
        car2.setCityBased("Berlin");
        carRepository.save(car2);

        Car car3 = new Car();
        car3.setCityBased("Munich");
        carRepository.save(car3);

        List<Car> berlinCars = carRepository.findByCityBased("Berlin");
        assertEquals(2, berlinCars.size());
    }
}
