package com.venediktov.carservice;

import com.venediktov.carservice.model.Car;
import com.venediktov.carservice.model.Customer;
import com.venediktov.carservice.services.CarService;
import com.venediktov.carservice.services.PriceService;
import com.venediktov.carservice.services.CustomerService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {
	@InjectMocks
	public PriceService priceService;

	private Car car;

	@BeforeEach
	public void prepare() {
		this.car = new Car();
		this.car.setDailyPrice(java.math.BigDecimal.valueOf(100));
	}

	@Test
	void verifyStandardPriceForCar() {
		this.car.setProductionYear(2016);
		java.math.BigDecimal finalPrice = this.priceService.calculateDailyPrice(this.car);
		Assert.isTrue(finalPrice.compareTo(java.math.BigDecimal.valueOf(100.0)) == 0, "Price should be 100");
	}

	@Test
	void verifyPriceForOldCar() {
		this.car.setProductionYear(2000); 
		java.math.BigDecimal finalPrice = this.priceService.calculateDailyPrice(this.car);
		Assert.isTrue(finalPrice.compareTo(java.math.BigDecimal.valueOf(90.0)) == 0, "Price should be 90");
	}
}
