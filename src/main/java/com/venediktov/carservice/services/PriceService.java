package com.venediktov.carservice.services;

import com.venediktov.carservice.model.Car;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;

@Service
@AllArgsConstructor
public class PriceService {


    public BigDecimal calculateDailyPrice(Car car) {
        BigDecimal basePrice = car.getDailyPrice() != null ? car.getDailyPrice() : BigDecimal.valueOf(100);
        

        int carProductionYear = car.getProductionYear();
        int currentYear = Year.now().getValue();
        double discount = 0.0;
        
        if (currentYear - carProductionYear > 10) {
            discount = 0.10; 
        }
        
        return basePrice.multiply(BigDecimal.valueOf(1.0 - discount));
    }
}
