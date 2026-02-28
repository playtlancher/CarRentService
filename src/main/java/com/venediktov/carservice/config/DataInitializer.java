package com.venediktov.carservice.config;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.model.User;
import com.venediktov.carservice.repositories.CarRepository;
import com.venediktov.carservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CarRepository carRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@carservice.com");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
            System.out.println("Default admin user created: admin/admin123");
        }

        if (carRepository.count() == 0) {
            Car car1 = new Car();
            car1.setBrand("Tesla");
            car1.setModel("Model S");
            car1.setProductionYear(2023);
            car1.setDailyPrice(BigDecimal.valueOf(150));
            car1.setCityBased("New York");
            car1.setStatus(Car.CarStatus.AVAILABLE);
            car1.setImageUrl("https://images.unsplash.com/photo-1617788138017-80ad42243c59?auto=format&fit=crop&q=80&w=800");

            Car car2 = new Car();
            car2.setBrand("BMW");
            car2.setModel("M4");
            car2.setProductionYear(2022);
            car2.setDailyPrice(BigDecimal.valueOf(120));
            car2.setCityBased("Los Angeles");
            car2.setStatus(Car.CarStatus.AVAILABLE);
            car2.setImageUrl("https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&q=80&w=800");

            Car car3 = new Car();
            car3.setBrand("Mercedes");
            car3.setModel("AMG GT");
            car3.setProductionYear(2024);
            car3.setDailyPrice(BigDecimal.valueOf(250));
            car3.setCityBased("Miami");
            car3.setStatus(Car.CarStatus.AVAILABLE);
            car3.setImageUrl("https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&q=80&w=800");

            carRepository.save(car1);
            carRepository.save(car2);
            carRepository.save(car3);
            System.out.println("Sample cars added to the database.");
        }
    }
}
