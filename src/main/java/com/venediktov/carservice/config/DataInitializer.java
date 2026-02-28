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
            car1.setVinCode("5YJSA1E26HF123456");
            car1.setProductionYear(2023);
            car1.setDailyPrice(BigDecimal.valueOf(150));
            car1.setCityBased("New York");
            car1.setStatus(Car.CarStatus.AVAILABLE);
            car1.setImageUrl("https://images.unsplash.com/photo-1617788138017-80ad42243c59?auto=format&fit=crop&q=80&w=800");
            car1.setDescription("Electric luxury sedan");
            car1.setLatitude(40.7128);
            car1.setLongitude(-74.0060);

            Car car2 = new Car();
            car2.setBrand("BMW");
            car2.setModel("M4");
            car2.setVinCode("WBS83AV09NCK12345");
            car2.setProductionYear(2022);
            car2.setDailyPrice(BigDecimal.valueOf(120));
            car2.setCityBased("Los Angeles");
            car2.setStatus(Car.CarStatus.AVAILABLE);
            car2.setImageUrl("https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?auto=format&fit=crop&q=80&w=800");
            car2.setDescription("Sport coupe");
            car2.setLatitude(34.0522);
            car2.setLongitude(-118.2437);

            Car car3 = new Car();
            car3.setBrand("Mercedes");
            car3.setModel("AMG GT");
            car3.setVinCode("WDD29KKG8KF123456");
            car3.setProductionYear(2024);
            car3.setDailyPrice(BigDecimal.valueOf(250));
            car3.setCityBased("Miami");
            car3.setStatus(Car.CarStatus.AVAILABLE);
            car3.setImageUrl("https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&q=80&w=800");
            car3.setDescription("High-performance GT");
            car3.setLatitude(25.7617);
            car3.setLongitude(-80.1918);

            Car car4 = new Car();
            car4.setBrand("Audi");
            car4.setModel("RS6 Avant");
            car4.setVinCode("WUAZZZ4GXNN123456");
            car4.setProductionYear(2023);
            car4.setDailyPrice(BigDecimal.valueOf(180));
            car4.setCityBased("Kyiv");
            car4.setStatus(Car.CarStatus.AVAILABLE);
            car4.setImageUrl("https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?auto=format&fit=crop&q=80&w=800");
            car4.setDescription("Performance wagon");
            car4.setLatitude(50.4501);
            car4.setLongitude(30.5234);

            Car car5 = new Car();
            car5.setBrand("Porsche");
            car5.setModel("911 Carrera");
            car5.setVinCode("WP0ZZZ99ZPS123456");
            car5.setProductionYear(2024);
            car5.setDailyPrice(BigDecimal.valueOf(320));
            car5.setCityBased("Berlin");
            car5.setStatus(Car.CarStatus.AVAILABLE);
            car5.setImageUrl("https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?auto=format&fit=crop&q=80&w=800");
            car5.setDescription("Iconic sports car");
            car5.setLatitude(52.5200);
            car5.setLongitude(13.4050);

            Car car6 = new Car();
            car6.setBrand("Volkswagen");
            car6.setModel("ID.4");
            car6.setVinCode("WVGZZZ5NZNM123456");
            car6.setProductionYear(2023);
            car6.setDailyPrice(BigDecimal.valueOf(95));
            car6.setCityBased("Lviv");
            car6.setStatus(Car.CarStatus.AVAILABLE);
            car6.setImageUrl("https://images.unsplash.com/photo-1619767886558-efdc259cde1a?auto=format&fit=crop&q=80&w=800");
            car6.setDescription("Electric SUV");
            car6.setLatitude(49.8397);
            car6.setLongitude(24.0297);

            Car car7 = new Car();
            car7.setBrand("Toyota");
            car7.setModel("Camry");
            car7.setVinCode("4T1BF1FK5NU123456");
            car7.setProductionYear(2022);
            car7.setDailyPrice(BigDecimal.valueOf(65));
            car7.setCityBased("Odesa");
            car7.setStatus(Car.CarStatus.AVAILABLE);
            car7.setImageUrl("https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&q=80&w=800");
            car7.setDescription("Reliable sedan");
            car7.setLatitude(46.4825);
            car7.setLongitude(30.7233);

            carRepository.save(car1);
            carRepository.save(car2);
            carRepository.save(car3);
            carRepository.save(car4);
            carRepository.save(car5);
            carRepository.save(car6);
            carRepository.save(car7);
            System.out.println("Sample cars added to the database.");
        }
    }
}
