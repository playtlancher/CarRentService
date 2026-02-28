package com.venediktov.carservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class CarServiceApplication {
	@Autowired(required = false)
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(CarServiceApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	void runAfterStartup() {
	}

	void createTables() {
		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS car(" +
				"id SERIAL PRIMARY KEY," +
				"brand VARCHAR(255)," +
				"model VARCHAR(255)," +
				"vin_code VARCHAR(255)," +
				"city_based VARCHAR(255)," +
				"production_year INTEGER," +
				"image_url VARCHAR(255)," +
				"created_at TIMESTAMP," +
				"updated_at TIMESTAMP" +
				")");

		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customer(" +
				"id SERIAL PRIMARY KEY," +
				"name VARCHAR(255)," +
				"email VARCHAR(255)," +
				"rent_amount_times INTEGER," +
				"created_at TIMESTAMP," +
				"updated_at TIMESTAMP" +
				")");

		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS car_rent(" +
				"id SERIAL PRIMARY KEY," +
				"car_id BIGINT REFERENCES car(id)," +
				"customer_id BIGINT REFERENCES customer(id)," +
				"start_date DATE," +
				"end_date DATE," +
				"created_at TIMESTAMP," +
				"updated_at TIMESTAMP" +
				")");
	}

	void insertInitialData() {
		if (!existsByVin("5342534534ae543")) {
			jdbcTemplate.update(
							"INSERT INTO car (production_year, vin_code, image_url, brand, model, city_based, created_at, " +
											"updated_at) " +
											"VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
							2005, "5342534534ae543", null, "opel", "astra", "New York"
			);
		}
		if (!existsEmail("petro@gmail.com")) {
			jdbcTemplate.update(
							"INSERT INTO customer (name, email, rent_amount_times, created_at, updated_at) " +
											"VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
							"petro", "petro@gmail.com", 0
			);
		}

		jdbcTemplate.update(
						"INSERT INTO car_rent (" +
										"car_id, customer_id, start_date, end_date, created_at, updated_at" +
										") " +
										"SELECT " +
										"c.id, " +
										"cu.id, " +
										"CURRENT_DATE - 5, " +
										"CURRENT_DATE + 3, " +
										"CURRENT_TIMESTAMP, " +
										"CURRENT_TIMESTAMP " +
										"FROM car c, customer cu " +
										"WHERE c.vin_code = ? " +
										"AND cu.email = ?",
						"5342534534ae543",
						"petro@gmail.com"
		);
	}

	private boolean existsByVin(String vinCode) {
		Integer count = jdbcTemplate.queryForObject(
						"SELECT COUNT(*) FROM car WHERE vin_code = ?",
						Integer.class,
						vinCode
		);
		return count != null && count > 0;
	}
	private boolean existsEmail(String email) {
		Integer count = jdbcTemplate.queryForObject(
						"SELECT COUNT(*) FROM customer WHERE email = ?",
						Integer.class,
						email
		);
		return count != null && count > 0;
	}

	public static final int CARS_AMOUNT_FOR_LOAD_TEST = 5_000;
	public static final int RENTS_AMOUNTS_FOR_LOAD_TESTS = 30_000;

	void insertCarsLoadTestingData() {
		Integer count = jdbcTemplate.queryForObject(
						"SELECT COUNT(*) FROM car",
						Integer.class
		);
		if (count != null && count >= CARS_AMOUNT_FOR_LOAD_TEST) {
			return;
		}
		String[] cities = {
						"New York",
						"Los Angeles",
						"Chicago",
						"Houston",
						"Phoenix"
		};

		String[] brands = {
						"Toyota",
						"Honda",
						"Ford",
						"BMW",
						"Mercedes",
						"Audi",
						"Volkswagen",
						"Hyundai",
						"Kia",
						"Nissan"
		};
		String[] models = {
						"Sedan",
						"SUV",
						"Hatchback",
						"Coupe",
						"Wagon"
		};

		Random random = new Random();

		String sql = "INSERT INTO car (" +
										"production_year, " +
										"vin_code, " +
										"image_url, " +
										"brand, " +
										"model, " +
										"city_based, " +
										"created_at, " +
										"updated_at" +
										") VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

		List<Object[]> batchArgs = new ArrayList<>(CARS_AMOUNT_FOR_LOAD_TEST);

		for (int i = 0; i < CARS_AMOUNT_FOR_LOAD_TEST; i++) {
			int year = 1998 + random.nextInt(27); 
			String vin = UUID.randomUUID().toString().replace("-", "").substring(0, 17);

			String brand = brands[random.nextInt(brands.length)];
			String model = models[random.nextInt(models.length)];
			String city = cities[random.nextInt(cities.length)];

			batchArgs.add(new Object[]{
							year,
							vin,
							null,
							brand,
							model,
							city
			});
		}

		jdbcTemplate.batchUpdate(sql, batchArgs);
	}

	void insertRentLoadTestingData() {
		Integer rentCount = jdbcTemplate.queryForObject(
						"SELECT COUNT(*) FROM car_rent",
						Integer.class
		);

		if (rentCount != null && rentCount > RENTS_AMOUNTS_FOR_LOAD_TESTS) {
			return; 
		}

		List<Long> carIds = jdbcTemplate.queryForList(
						"SELECT id FROM car",
						Long.class
		);

		List<Long> customerIds = jdbcTemplate.queryForList(
						"SELECT id FROM customer",
						Long.class
		);

		if (customerIds.size() < 100) {
			insertRandomCustomers(100 - customerIds.size());
			customerIds = jdbcTemplate.queryForList(
							"SELECT id FROM customer",
							Long.class
			);
		}

		Random random = new Random();

		String sql =
						"INSERT INTO car_rent (" +
										"car_id, customer_id, start_date, end_date, created_at, updated_at" +
										") VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

		List<Object[]> batch = new ArrayList<>();

		for (int i = 0; i < RENTS_AMOUNTS_FOR_LOAD_TESTS; i++) {
			Long carId = carIds.get(random.nextInt(carIds.size()));
			Long customerId = customerIds.get(random.nextInt(customerIds.size()));

			LocalDate startDate = LocalDate.now()
							.minusDays(random.nextInt(365));
			LocalDate endDate = startDate.plusDays(1 + random.nextInt(30));

			batch.add(new Object[]{
							carId,
							customerId,
							Date.valueOf(startDate),
							Date.valueOf(endDate)
			});
		}

		jdbcTemplate.batchUpdate(sql, batch);
	}


	void insertRandomCustomers(int count) {
		String sql =
						"INSERT INTO customer (" +
										"name, email, rent_amount_times, created_at, updated_at" +
										") VALUES (?, ?, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

		List<Object[]> batch = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			String name = "user_" + UUID.randomUUID().toString().substring(0, 8);
			String email = name + "@example.com";

			batch.add(new Object[]{name, email});
		}
		jdbcTemplate.batchUpdate(sql, batch);
	}
}
